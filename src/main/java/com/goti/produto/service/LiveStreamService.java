package com.goti.produto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.produto.config.RabbitMQConfig;
import com.goti.produto.dto.MensagensDTO.MensagemRespostaDTO;
import com.goti.produto.dto.MensagensDTO.MensagemSolicitacaoDTO;
import com.goti.produto.model.LiveStream;
import com.goti.produto.repository.LiveStreamRepository;

@Service
public class LiveStreamService {

    private final LiveStreamRepository liveStreamRepository;
    private final RabbitTemplate rabbitTemplate;

    public LiveStreamService(LiveStreamRepository liveStreamRepository, RabbitTemplate rabbitTemplate) {
        this.liveStreamRepository = liveStreamRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    // CRUD de catálogo de Lives
    public LiveStream salvar(LiveStream live) {
        if (live.getVagasOcupadas() == null) {
            live.setVagasOcupadas(0);
        }
        return liveStreamRepository.save(live);
    }

    public List<LiveStream> listarTodas() {
        return liveStreamRepository.findAll();
    }

    public Optional<LiveStream> buscarPorId(String id) {
        return liveStreamRepository.findById(id);
    }

    // Consumidor da fila com concorrência gerenciada
    @RabbitListener(
            queues = RabbitMQConfig.FILA_SOLICITACAO,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void processarValidacaoEstoqueVagas(MensagemSolicitacaoDTO solicitacao) {
        try {
            executarAbatimentoVagas(solicitacao);
        } catch (ObjectOptimisticLockingFailureException e) {
            // Em caso de colisão simultânea de workers abatendo a mesma live
            enviarResposta(new MensagemRespostaDTO(
                    solicitacao.protocolo(),
                    false,
                    "Conflito de concorrência ao reservar vaga. Tente novamente."
            ));
        }
    }

    @Transactional
    protected void executarAbatimentoVagas(MensagemSolicitacaoDTO solicitacao) {
        Optional<LiveStream> liveOpt = liveStreamRepository.findById(solicitacao.liveId());

        if (liveOpt.isEmpty()) {
            enviarResposta(new MensagemRespostaDTO(
                    solicitacao.protocolo(),
                    false,
                    "Live/Produto não encontrado."
            ));
            return;
        }

        LiveStream live = liveOpt.get();

        if (live.temVagas(solicitacao.quantidade())) {
            live.ocuparVagas(solicitacao.quantidade());
            liveStreamRepository.save(live); // Atualiza com Optimistic Locking

            enviarResposta(new MensagemRespostaDTO(
                    solicitacao.protocolo(),
                    true,
                    "Vagas reservadas com sucesso!"
            ));
        } else {
            enviarResposta(new MensagemRespostaDTO(
                    solicitacao.protocolo(),
                    false,
                    "compra recusada, produto sem estoque / sala de transmissão lotada."
            ));
        }
    }

    private void enviarResposta(MensagemRespostaDTO resposta) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.VENDA_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_RESPOSTA,
                resposta
        );
    }
}