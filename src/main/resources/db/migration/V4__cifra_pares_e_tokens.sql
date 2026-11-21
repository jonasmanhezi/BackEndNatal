-- Quem tirou quem deixa de ficar legível no banco.
--  * O par é cifrado com o token pessoal de quem deu: só quem abre aquele link consegue ler.
--  * O token guardado para o organizador reenviar é cifrado com a ADMIN_KEY (variável no Render, fora do banco).
-- Sorteios feitos antes desta versão são apagados (os pares estavam abertos): é só sortear de novo.

DELETE FROM tb_atribuicao;
DELETE FROM tb_sorteio_membro;
UPDATE tb_configuracao SET status = 'OPEN', sorteado_em = NULL;

ALTER TABLE tb_atribuicao DROP COLUMN IF EXISTS receiver_id;
ALTER TABLE tb_atribuicao ADD COLUMN receiver_cifrado TEXT NOT NULL;

-- Tokens em texto da V3 somem; o organizador gera um link novo para esses cadastros.
ALTER TABLE tb_participante DROP COLUMN IF EXISTS token;
ALTER TABLE tb_participante ADD COLUMN token_cifrado TEXT;
