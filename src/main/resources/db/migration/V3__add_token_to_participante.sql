-- O organizador precisa reenviar o link pessoal de cada um (ex.: pelo WhatsApp) sem invalidar o anterior.
-- Por isso o token também fica guardado; a busca continua pelo hash.
-- Cadastros antigos ficam com token nulo: o organizador gera um link novo para eles.
ALTER TABLE tb_participante ADD COLUMN IF NOT EXISTS token VARCHAR(32);
