-- No Supabase, tabelas do schema public também ficam expostas pela API REST automática (chave anon).
-- RLS ligado e sem nenhuma policy = essa API não lê nem grava nada.
-- O backend conecta como dono das tabelas (usuário postgres), que não passa pelo RLS.
ALTER TABLE tb_configuracao ENABLE ROW LEVEL SECURITY;
ALTER TABLE tb_participante ENABLE ROW LEVEL SECURITY;
ALTER TABLE tb_atribuicao ENABLE ROW LEVEL SECURITY;
ALTER TABLE tb_sorteio_membro ENABLE ROW LEVEL SECURITY;
