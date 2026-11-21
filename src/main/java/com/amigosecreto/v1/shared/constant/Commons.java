package com.amigosecreto.v1.shared.constant;

public final class Commons {

    public static final String MSG_SORTEIO_MINIMO = "O sorteio precisa de pelo menos 3 pessoas.";

    public static final String MSG_SORTEIO_JA_FEITO = "O sorteio já foi feito. Fale com o organizador.";

    public static final String MSG_SAIR_DEPOIS_DO_SORTEIO =
            "O sorteio já foi feito. Peça ao organizador para reabrir as inscrições.";

    public static final String MSG_PESSOA_NAO_ENCONTRADA = "Pessoa não encontrada.";

    public static final String MSG_LINK_INVALIDO = "Link pessoal inválido.";

    public static final String MSG_CHAVE_INVALIDA = "Chave de organizador inválida.";

    public static final String MSG_FOTO_INVALIDA = "A foto precisa ser JPEG, PNG ou WebP.";

    public static final String MSG_FOTO_GRANDE = "A foto ficou grande demais. Tente outra.";

    public static final String MSG_DATA_INVALIDA = "Use datas no formato AAAA-MM-DD.";

    public static final String MSG_MUITAS_TENTATIVAS = "Muitas tentativas. Espere alguns minutos e tente de novo.";

    public static final String MSG_SORTEIO_SEM_LINK =
            "Gere um link novo para estas pessoas antes de sortear: %s.";

    public static final String MSG_NOME_REPETIDO =
            "Já tem alguém chamado %s no sorteio. Se for você, abra o seu link pessoal; se for outra pessoa, coloque o sobrenome.";

    public static final String MSG_SORTEIO_NOMES_REPETIDOS =
            "Tem nomes repetidos no sorteio: %s. Remova a pessoa duplicada antes de sortear.";

    /** ~200 KB de imagem viram ~270 mil caracteres em base64. */
    public static final int FOTO_MAX_CARACTERES = 280_000;

    private Commons() {
    }
}
