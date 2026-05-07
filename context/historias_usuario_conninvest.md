# ConnInvest — Documento de Histórias de Usuário e Requisitos Funcionais

## 1. Visão geral do produto

O ConnInvest é uma plataforma web voltada para conectar founders, startups, investidores, advisors e potenciais co-founders. A proposta é criar uma rede social orientada a dados, reputação e progresso real, permitindo que startups sejam descobertas por investidores e que founders encontrem investidores, sócios e parceiros estratégicos.

A plataforma não realiza transações financeiras nem investimentos diretamente. O objetivo é facilitar descoberta, reputação, conexão, comunicação e propostas iniciais.

## 2. Tipos de usuário

### 2.1 Usuário comum
Usuário autenticado na plataforma, com perfil pessoal criado.

### 2.2 Founder
Usuário que criou ou participa de uma ou mais startups.

### 2.3 Investidor
Usuário interessado em descobrir startups, acompanhar updates, receber propostas e se conectar com founders.

### 2.4 Advisor
Usuário que pode apoiar startups com experiência, rede de contatos ou conhecimento específico.

### 2.5 Co-founder em potencial
Usuário que está buscando participar de uma startup como sócio, seja com perfil técnico, comercial, produto, marketing ou operação.

## 3. Módulo de autenticação e onboarding

### US-001 — Criar conta na plataforma
**Como usuário**, quero criar uma conta para acessar a plataforma e configurar meu perfil.

#### Requisitos funcionais
- O sistema deve permitir cadastro com nome, e-mail e senha.
- O sistema deve permitir login com e-mail e senha.
- O sistema deve validar e-mail em formato válido.
- O sistema deve impedir cadastro com e-mail já utilizado.
- O sistema deve permitir logout.

#### Critérios de aceite
- Dado que informo dados válidos, quando finalizo o cadastro, então minha conta deve ser criada.
- Dado que uso um e-mail já cadastrado, quando tento criar conta, então o sistema deve exibir mensagem de erro.
- Dado que estou autenticado, quando clico em sair, então minha sessão deve ser encerrada.

---

### US-002 — Escolher tipo principal de perfil
**Como usuário**, quero escolher meu tipo principal de perfil para personalizar minha experiência na plataforma.

#### Requisitos funcionais
- O sistema deve permitir selecionar um ou mais tipos de perfil:
  - Founder
  - Investidor
  - Advisor
  - Buscando co-founder
  - Outro
- O usuário pode alterar essa escolha posteriormente.
- Nenhum tipo deve bloquear completamente o acesso às funcionalidades básicas, mas pode alterar recomendações e campos exibidos.

#### Critérios de aceite
- Dado que seleciono “Founder”, quando acesso a plataforma, então devo ver atalhos para criar startup.
- Dado que seleciono “Investidor”, quando acesso a plataforma, então devo ver atalhos para configurar tese de investimento.
- Dado que altero meu tipo depois, quando salvo, então a plataforma deve refletir a nova configuração.

---

## 4. Módulo de perfil pessoal

### US-003 — Criar e editar perfil pessoal
**Como usuário**, quero criar e editar meu perfil pessoal para apresentar minha trajetória, interesses e habilidades.

#### Requisitos funcionais
- O perfil pessoal deve conter:
  - Nome
  - Foto
  - Bio
  - Localização
  - Tipo de perfil
  - Experiências anteriores
  - Skills principais
  - Interesses
  - Links externos
- Nenhum campo além do nome deve ser obrigatório no protótipo.
- O usuário deve poder editar o perfil a qualquer momento.

#### Critérios de aceite
- Dado que acesso meu perfil, quando clico em editar, então posso alterar meus dados.
- Dado que deixo campos opcionais em branco, quando salvo, então o perfil deve ser salvo normalmente.
- Dado que adiciono skills, quando visualizo meu perfil, então elas devem aparecer.

---

### US-004 — Indicar interesse em buscar co-founder
**Como founder ou usuário empreendedor**, quero indicar que estou buscando co-founder para ser encontrado por pessoas compatíveis.

#### Requisitos funcionais
- O perfil deve permitir marcar a opção “Buscando co-founder”.
- O usuário deve informar opcionalmente:
  - Tipo de co-founder desejado
  - Área desejada: tech, business, produto, marketing, vendas, operação, outra
  - Nível de dedicação esperado: full-time, part-time, advisor, indefinido
  - Descrição do que procura
- Essa informação deve aparecer no perfil e na área de busca de co-founders.

#### Critérios de aceite
- Dado que marco “Buscando co-founder”, quando salvo meu perfil, então meu perfil deve aparecer nas buscas relacionadas.
- Dado que informo uma descrição do que procuro, quando alguém acessa meu perfil, então essa descrição deve estar visível.

---

### US-005 — Exibir histórico de startups vinculadas ao usuário
**Como usuário**, quero ver no perfil pessoal as startups atuais e anteriores vinculadas a mim para construir reputação empreendedora.

#### Requisitos funcionais
- O perfil deve listar startups associadas ao usuário.
- Cada startup deve exibir o papel do usuário:
  - Founder
  - Co-founder
  - Advisor
  - Membro
- Deve ser possível acessar o perfil da startup a partir do perfil pessoal.

#### Critérios de aceite
- Dado que estou vinculado a uma startup, quando acesso meu perfil, então a startup deve aparecer na minha lista.
- Dado que clico em uma startup listada, então devo ser direcionado ao perfil da startup.

---

## 5. Módulo de perfil de startup

### US-006 — Criar perfil de startup
**Como founder**, quero criar um perfil de startup para apresentar meu negócio a investidores, advisors e possíveis co-founders.

#### Requisitos funcionais
- O usuário autenticado deve poder criar uma startup.
- O criador da startup deve ser definido automaticamente como Owner.
- O perfil da startup deve conter:
  - Nome
  - Logo
  - Descrição curta
  - Descrição completa
  - Setor
  - Estágio
  - Localização
  - Site ou link externo
  - Objetivo atual
  - Métricas principais
- Nenhum campo além do nome deve ser obrigatório no protótipo.

#### Critérios de aceite
- Dado que sou usuário autenticado, quando crio uma startup com nome válido, então o perfil da startup deve ser criado.
- Dado que crio uma startup, então devo aparecer como Owner.
- Dado que acesso o perfil da startup, então devo ver as informações preenchidas.

---

### US-007 — Editar perfil de startup
**Como Owner ou Admin**, quero editar o perfil da startup para manter as informações atualizadas.

#### Requisitos funcionais
- Apenas usuários com permissão de Owner ou Admin podem editar o perfil da startup.
- O sistema deve permitir alterar descrição, setor, estágio, métricas, objetivo atual e links.
- O sistema deve registrar visualmente a data da última atualização.

#### Critérios de aceite
- Dado que sou Owner, quando edito os dados, então as alterações devem ser salvas.
- Dado que sou Viewer, quando acesso o perfil da startup, então não devo ver opção de edição.
- Dado que salvo alterações, quando retorno ao perfil, então a data de atualização deve ser exibida.

---

### US-008 — Definir estágio da startup
**Como founder**, quero informar o estágio da startup para ajudar investidores e co-founders a entenderem o momento do negócio.

#### Requisitos funcionais
- O sistema deve permitir selecionar estágio:
  - Ideia
  - MVP
  - Primeiros usuários
  - Receita inicial
  - Tração
  - Escala
- O estágio deve aparecer no perfil, nos filtros de busca e nos rankings.

#### Critérios de aceite
- Dado que seleciono “MVP”, quando salvo, então o estágio deve aparecer no perfil.
- Dado que um investidor filtra startups por “MVP”, então minha startup deve aparecer se estiver nesse estágio.

---

### US-009 — Registrar métricas principais da startup
**Como founder**, quero informar métricas principais da startup para demonstrar progresso real.

#### Requisitos funcionais
- O sistema deve permitir informar métricas como:
  - Receita mensal
  - Número de usuários
  - Crescimento percentual
  - Número de clientes
  - MRR, quando aplicável
  - Churn, quando aplicável
- As métricas podem ser opcionais.
- O sistema deve permitir ocultar ou exibir métricas publicamente.
- Métricas devem ser usadas como base visual para ranking de performance no protótipo.

#### Critérios de aceite
- Dado que informo receita mensal, quando salvo, então ela deve aparecer se eu definir como pública.
- Dado que marco uma métrica como privada, quando outro usuário acessa meu perfil, então essa métrica não deve ser exibida.

---

## 6. Módulo de membros e permissões da startup

### US-010 — Convidar membro para uma startup
**Como Owner ou Admin**, quero convidar outros usuários para participar do perfil da startup.

#### Requisitos funcionais
- O sistema deve permitir buscar usuário por nome ou e-mail.
- O Owner/Admin deve selecionar o papel do convidado:
  - Admin
  - Editor
  - Viewer
  - Advisor
  - Co-founder
- O usuário convidado deve receber um convite.
- O convidado deve poder aceitar ou recusar.

#### Critérios de aceite
- Dado que envio um convite, quando o usuário convidado acessa sua área de convites, então deve ver o convite pendente.
- Dado que o usuário aceita o convite, então ele deve aparecer na lista de membros da startup.
- Dado que o usuário recusa, então ele não deve ser vinculado à startup.

---

### US-011 — Controlar permissões por papel
**Como Owner**, quero controlar permissões dos membros para proteger a credibilidade e a gestão da startup.

#### Requisitos funcionais
- Owner pode editar tudo, gerenciar membros e excluir a startup.
- Admin pode editar perfil e gerenciar posts.
- Editor pode criar posts em nome da startup.
- Viewer pode apenas visualizar informações internas permitidas.
- Advisor pode aparecer publicamente como apoiador, sem permissões administrativas por padrão.

#### Critérios de aceite
- Dado que sou Editor, quando tento editar permissões de outro usuário, então o sistema deve impedir.
- Dado que sou Admin, quando crio post pela startup, então o post deve ser publicado.
- Dado que sou Viewer, quando acesso a startup, então não devo conseguir publicar em nome dela.

---

## 7. Módulo de feed e posts

### US-012 — Visualizar feed principal
**Como usuário**, quero visualizar um feed com posts de founders, startups e investidores para acompanhar o ecossistema.

#### Requisitos funcionais
- O feed deve exibir posts de usuários e startups.
- O feed deve diferenciar visualmente post pessoal e post de startup.
- O feed deve permitir curtir, comentar e seguir o autor.
- O feed deve exibir data, autor e tipo de post.

#### Critérios de aceite
- Dado que acesso a página inicial, então devo ver uma lista de posts recentes.
- Dado que um post foi feito por uma startup, então o nome da startup deve aparecer como autora.
- Dado que um post foi feito por um founder, então o nome do usuário deve aparecer como autor.

---

### US-013 — Criar post livre
**Como usuário ou startup**, quero criar posts livres para compartilhar bastidores, aprendizados, novidades e rotina.

#### Requisitos funcionais
- O sistema deve permitir criar post com texto livre.
- O sistema deve permitir adicionar imagem no protótipo de forma visual/simulada.
- O usuário deve escolher se publica como perfil pessoal ou como startup vinculada, caso tenha permissão.
- O post livre pode ser usado para:
  - Bastidores
  - Cultura
  - Fotos com equipe
  - Fechamento de clientes
  - Aprendizados
  - Novidades gerais

#### Critérios de aceite
- Dado que escrevo um texto e publico, então o post deve aparecer no feed.
- Dado que tenho uma startup vinculada, quando crio post, então posso escolher postar como pessoa ou startup.
- Dado que não tenho permissão na startup, então não devo conseguir postar em nome dela.

---

### US-014 — Criar update estruturado da startup
**Como founder**, quero criar updates estruturados para mostrar progresso real da startup.

#### Requisitos funcionais
- O sistema deve permitir criar update estruturado com campos:
  - Tipo de update
  - Métrica relacionada
  - Valor anterior
  - Valor atual
  - Descrição do progresso
  - Data do marco
- Tipos de update sugeridos:
  - Crescimento
  - Receita
  - Usuários
  - Produto
  - Cliente
  - Captação
  - Parceria
  - Lançamento
- Updates estruturados devem aparecer no feed e no perfil da startup.
- Updates estruturados devem impactar visualmente o ranking de performance no protótipo.

#### Critérios de aceite
- Dado que crio um update de crescimento, quando publico, então ele aparece no feed como update estruturado.
- Dado que acesso o perfil da startup, então vejo o histórico de updates.
- Dado que uma startup publica updates consistentes, então ela pode receber destaque visual no ranking.

---

### US-015 — Interagir com posts
**Como usuário**, quero curtir e comentar posts para interagir com founders, startups e investidores.

#### Requisitos funcionais
- O sistema deve permitir curtir posts.
- O sistema deve permitir remover curtida.
- O sistema deve permitir comentar posts.
- O sistema deve exibir quantidade de curtidas e comentários.
- Comentários devem exibir autor e data.

#### Critérios de aceite
- Dado que clico em curtir, então o número de curtidas deve aumentar.
- Dado que comento em um post, então meu comentário deve aparecer abaixo do post.
- Dado que removo uma curtida, então o número de curtidas deve diminuir.

---

## 8. Módulo de seguidores

### US-016 — Seguir usuários e startups
**Como usuário**, quero seguir founders, investidores e startups para acompanhar seus updates.

#### Requisitos funcionais
- O sistema deve permitir seguir e deixar de seguir usuários.
- O sistema deve permitir seguir e deixar de seguir startups.
- O perfil deve exibir número de seguidores.
- O feed pode priorizar conteúdos de perfis seguidos.

#### Critérios de aceite
- Dado que clico em seguir uma startup, então ela deve aparecer como seguida.
- Dado que uma startup seguida publica update, então o update deve aparecer no meu feed.
- Dado que deixo de seguir, então não devo mais receber prioridade daquele perfil.

---

## 9. Módulo de rankings

### US-017 — Visualizar ranking de performance
**Como investidor**, quero visualizar startups com melhor performance para descobrir oportunidades com base em progresso real.

#### Requisitos funcionais
- O sistema deve exibir ranking de performance.
- O ranking deve considerar visualmente:
  - Crescimento
  - Métricas
  - Consistência de updates
  - Marcos atingidos
- O protótipo pode usar dados simulados.
- Cada item do ranking deve permitir acessar o perfil da startup.

#### Critérios de aceite
- Dado que acesso rankings, quando seleciono “Performance”, então vejo uma lista ordenada de startups.
- Dado que clico em uma startup do ranking, então sou direcionado ao perfil dela.

---

### US-018 — Visualizar ranking de popularidade
**Como usuário da comunidade**, quero visualizar startups populares para acompanhar quem está chamando atenção.

#### Requisitos funcionais
- O sistema deve exibir ranking de popularidade.
- O ranking deve considerar visualmente:
  - Curtidas
  - Comentários
  - Seguidores
  - Crescimento de seguidores
- O ranking deve ser separado do ranking de performance.

#### Critérios de aceite
- Dado que acesso rankings, quando seleciono “Popularidade”, então vejo startups com maior engajamento.
- Dado que uma startup tem muitos likes e comentários, então ela deve aparecer bem posicionada no ranking popular.

---

### US-019 — Visualizar ranking trending
**Como investidor ou founder**, quero visualizar startups que estão crescendo rapidamente agora para identificar oportunidades emergentes.

#### Requisitos funcionais
- O sistema deve exibir ranking trending.
- O ranking deve considerar visualmente:
  - Crescimento recente
  - Aumento recente de engajamento
  - Novos seguidores
  - Updates recentes relevantes
- O ranking deve ter foco em momentum, não apenas números absolutos.

#### Critérios de aceite
- Dado que acesso “Trending”, então vejo startups em crescimento recente.
- Dado que uma startup teve aumento recente de engajamento, então ela pode aparecer no ranking trending.

---

### US-020 — Exibir badges de destaque
**Como startup**, quero receber badges para demonstrar conquistas e aumentar credibilidade.

#### Requisitos funcionais
- O sistema deve exibir badges visuais como:
  - Top Growth
  - Trending
  - Most Consistent
  - Popular
- Badges devem aparecer no perfil da startup e nos rankings.
- No protótipo, os badges podem ser calculados de forma simulada.

#### Critérios de aceite
- Dado que uma startup está bem colocada no ranking de performance, então pode exibir badge Top Growth.
- Dado que uma startup está no ranking trending, então pode exibir badge Trending.

---

## 10. Módulo de busca e descoberta

### US-021 — Buscar startups
**Como investidor, advisor ou founder**, quero buscar startups por critérios relevantes para encontrar oportunidades e conexões.

#### Requisitos funcionais
- O sistema deve permitir buscar startups por texto.
- O sistema deve permitir filtrar por:
  - Setor
  - Estágio
  - Localização
  - Objetivo atual
  - Ranking
  - Métricas públicas
- Os resultados devem exibir cards com resumo da startup.

#### Critérios de aceite
- Dado que filtro por setor “SaaS”, então devo ver startups desse setor.
- Dado que busco por nome, então startups compatíveis devem aparecer.
- Dado que clico em um resultado, então sou levado ao perfil da startup.

---

### US-022 — Buscar founders
**Como usuário**, quero buscar founders por habilidades, interesses e experiência para encontrar potenciais conexões ou co-founders.

#### Requisitos funcionais
- O sistema deve permitir buscar usuários por nome.
- O sistema deve permitir filtrar por:
  - Tipo de perfil
  - Skills
  - Interesse em co-founder
  - Localização
  - Experiência
- O resultado deve exibir nome, bio curta, skills e startups vinculadas.

#### Critérios de aceite
- Dado que filtro por skill “tech”, então usuários com essa skill devem aparecer.
- Dado que filtro por “buscando co-founder”, então apenas usuários com essa intenção devem ser destacados.

---

### US-023 — Buscar investidores
**Como founder**, quero buscar investidores por tese, setor, estágio e ticket para encontrar investidores compatíveis com minha startup.

#### Requisitos funcionais
- O sistema deve permitir buscar investidores por nome.
- O sistema deve permitir filtrar por:
  - Tipo de investidor
  - Setores de interesse
  - Estágios de interesse
  - Ticket médio
  - Localização
  - O que pode agregar
  - Estilo de atuação
- O resultado deve exibir cards de investidores com informações essenciais.

#### Critérios de aceite
- Dado que filtro investidores interessados em fintech, então devo ver investidores com essa tese.
- Dado que filtro por ticket médio, então devo ver investidores compatíveis.
- Dado que clico em um investidor, então acesso seu perfil completo.

---

## 11. Módulo de perfil de investidor

### US-024 — Configurar perfil de investidor
**Como investidor**, quero configurar minha tese de investimento para receber conexões e propostas mais alinhadas.

#### Requisitos funcionais
- O perfil de investidor deve conter:
  - Tipo de investidor: angel, VC, advisor, scout, outro
  - Setores de interesse
  - Estágios de interesse
  - Ticket médio
  - Geografia de atuação
  - Histórico de investimentos
  - O que pode agregar
  - Estilo de atuação: hands-on, hands-off, híbrido
  - Preferência de contato
- Campos podem ser opcionais no protótipo.

#### Critérios de aceite
- Dado que preencho setores de interesse, quando salvo, então eles aparecem no meu perfil.
- Dado que um founder busca investidores por setor, então meu perfil pode aparecer se for compatível.

---

### US-025 — Exibir histórico de investimentos
**Como investidor**, quero exibir meu histórico de investimentos para demonstrar credibilidade e experiência.

#### Requisitos funcionais
- O investidor deve poder adicionar startups investidas.
- Cada item pode conter:
  - Nome da startup
  - Setor
  - Ano
  - Status: ativa, exit, falhou, não informado
  - Papel do investidor
- O histórico deve ser exibido no perfil do investidor.

#### Critérios de aceite
- Dado que adiciono uma startup investida, então ela deve aparecer no meu histórico.
- Dado que não informo histórico, então o perfil deve continuar válido.

---

### US-026 — Informar valor agregado pelo investidor
**Como investidor**, quero informar como posso ajudar startups além do capital para atrair founders alinhados.

#### Requisitos funcionais
- O investidor deve poder selecionar áreas de apoio:
  - Network
  - Produto
  - Growth
  - Tecnologia
  - Vendas
  - Fundraising
  - Estratégia
  - Operações
- Deve poder escrever descrição livre sobre como agrega valor.

#### Critérios de aceite
- Dado que seleciono “Growth” e “Network”, então essas informações aparecem no perfil.
- Dado que um founder busca investidores que agregam em growth, então meu perfil pode aparecer.

---

## 12. Módulo de mensagens

### US-027 — Enviar mensagem direta
**Como usuário**, quero enviar mensagens diretas para conversar com founders, investidores, advisors ou co-founders.

#### Requisitos funcionais
- O sistema deve permitir iniciar conversa com outro usuário.
- A conversa deve exibir histórico de mensagens.
- O sistema deve permitir responder mensagens.
- O sistema deve indicar mensagens não lidas.
- A mensagem direta deve ser separada do sistema de propostas.

#### Critérios de aceite
- Dado que acesso o perfil de um usuário, quando clico em mensagem, então posso iniciar conversa.
- Dado que envio mensagem, então ela aparece no histórico da conversa.
- Dado que recebo mensagem, então devo ver indicação de mensagem não lida.

---

### US-028 — Controlar qualidade mínima para contato
**Como investidor**, quero evitar spam para receber mensagens mais relevantes.

#### Requisitos funcionais
- O sistema deve permitir indicar preferência de contato.
- O protótipo deve exibir aviso quando o perfil do remetente estiver incompleto.
- O sistema pode sugerir completar perfil antes de enviar proposta.
- Para propostas, o sistema deve incentivar preenchimento de dados mínimos.

#### Critérios de aceite
- Dado que tento enviar proposta com perfil incompleto, então vejo um aviso recomendando completar informações.
- Dado que o investidor prefere propostas estruturadas, então essa preferência deve aparecer no perfil dele.

---

## 13. Módulo de propostas

### US-029 — Enviar proposta de investimento para investidor
**Como founder**, quero enviar uma proposta estruturada para um investidor para apresentar minha startup com clareza.

#### Requisitos funcionais
- A proposta de investimento deve conter:
  - Startup relacionada
  - Breve pitch
  - Estágio atual
  - Setor
  - Métricas principais
  - Valor buscado, opcional
  - Uso pretendido do capital, opcional
  - Por que aquele investidor faz sentido
- O sistema deve permitir salvar e enviar a proposta.
- O investidor deve receber a proposta em área específica.

#### Critérios de aceite
- Dado que sou founder de uma startup, quando acesso perfil de investidor, então posso enviar proposta de investimento.
- Dado que envio proposta, então o investidor deve recebê-la.
- Dado que não informo valor buscado, então ainda posso enviar se o campo for opcional.

---

### US-030 — Enviar proposta para co-founder
**Como founder**, quero enviar proposta para alguém se tornar co-founder da startup.

#### Requisitos funcionais
- A proposta deve conter:
  - Startup relacionada
  - Papel esperado
  - Perfil desejado
  - Dedicação esperada
  - Percentual sugerido, opcional
  - Descrição do momento da startup
- O destinatário deve poder aceitar conversa, recusar ou pedir mais informações.

#### Critérios de aceite
- Dado que encontro um usuário buscando co-founder, quando envio proposta, então ele deve recebê-la.
- Dado que o usuário responde, então a resposta deve aparecer para mim.

---

### US-031 — Enviar proposta para advisor
**Como founder**, quero enviar proposta para um advisor apoiar minha startup.

#### Requisitos funcionais
- A proposta deve conter:
  - Startup relacionada
  - Área de apoio desejada
  - Tipo de relacionamento esperado
  - Dedicação estimada
  - Possível contrapartida, opcional
- O advisor deve receber e responder.

#### Critérios de aceite
- Dado que acesso perfil de advisor, quando envio proposta, então ele deve recebê-la.
- Dado que o advisor aceita conversar, então uma conversa deve ser criada ou destacada.

---

### US-032 — Gerenciar propostas recebidas
**Como usuário**, quero visualizar e gerenciar propostas recebidas para organizar oportunidades.

#### Requisitos funcionais
- O sistema deve ter uma tela de propostas recebidas.
- Propostas devem ter status:
  - Recebida
  - Visualizada
  - Aceita para conversa
  - Recusada
  - Arquivada
- O usuário deve poder responder a uma proposta.

#### Critérios de aceite
- Dado que recebo uma proposta, então ela aparece na tela de propostas.
- Dado que marco como recusada, então o status deve ser atualizado.
- Dado que aceito conversar, então o remetente deve ser notificado.

---

## 14. Módulo de co-founder matching

### US-033 — Visualizar startups buscando co-founder
**Como usuário interessado em empreender**, quero ver startups que estão buscando co-founder para encontrar oportunidades.

#### Requisitos funcionais
- O sistema deve listar startups com status “Buscando co-founder”.
- Cada card deve exibir:
  - Nome da startup
  - Setor
  - Estágio
  - Tipo de co-founder buscado
  - Descrição curta
- Deve ser possível acessar o perfil da startup e enviar proposta ou mensagem.

#### Critérios de aceite
- Dado que acesso a área de co-founder matching, então vejo startups buscando sócios.
- Dado que filtro por “tech”, então vejo startups buscando co-founder técnico.

---

### US-034 — Visualizar founders disponíveis para co-founder
**Como founder**, quero ver pessoas abertas a serem co-founders para encontrar sócios compatíveis.

#### Requisitos funcionais
- O sistema deve listar usuários com indicação de interesse em co-founder.
- Cada card deve exibir:
  - Nome
  - Skills
  - Bio curta
  - Disponibilidade
  - Interesses
- Deve permitir enviar mensagem ou proposta.

#### Critérios de aceite
- Dado que acesso a lista de founders disponíveis, então vejo usuários marcados como buscando co-founder.
- Dado que clico em um perfil, então acesso detalhes do usuário.

---

### US-035 — Exibir sugestão de compatibilidade
**Como usuário**, quero ver uma indicação simples de compatibilidade para priorizar conexões mais promissoras.

#### Requisitos funcionais
- O protótipo deve exibir um score visual de compatibilidade.
- O score pode considerar de forma simulada:
  - Skills complementares
  - Setor de interesse
  - Estágio desejado
  - Disponibilidade
- Deve ser apresentado como indicador visual, sem necessidade de algoritmo real no protótipo.

#### Critérios de aceite
- Dado que vejo uma sugestão de co-founder, então deve aparecer um percentual ou nível de compatibilidade.
- Dado que as skills são complementares, então o score pode aparecer mais alto.

---

## 15. Módulo de reputação e scores

### US-036 — Exibir score da startup
**Como usuário**, quero ver um score resumido da startup para ter uma noção rápida de reputação, consistência e progresso.

#### Requisitos funcionais
- O perfil da startup deve exibir um score visual.
- O score pode considerar de forma conceitual:
  - Consistência de updates
  - Crescimento
  - Engajamento qualificado
  - Badges
- No protótipo, o score pode ser simulado.

#### Critérios de aceite
- Dado que acesso perfil de startup, então vejo um score visual.
- Dado que a startup possui updates e badges, então o score pode parecer mais alto.

---

### US-037 — Exibir score do founder
**Como investidor ou usuário**, quero ver um score do founder para avaliar consistência e reputação empreendedora.

#### Requisitos funcionais
- O perfil pessoal deve exibir score do founder.
- O score pode considerar de forma conceitual:
  - Startups vinculadas
  - Consistência de atividade
  - Engajamento
  - Histórico de participação
- No protótipo, o score pode ser visual/simulado.

#### Critérios de aceite
- Dado que acesso perfil de founder, então vejo um indicador de reputação.
- Dado que o founder tem startups e posts consistentes, então o score pode aparecer mais alto.

---

### US-038 — Exibir score de investidor
**Como founder**, quero ver informações de reputação do investidor para entender se ele é relevante e agrega valor.

#### Requisitos funcionais
- O perfil de investidor deve exibir um score ou indicador visual.
- O score pode considerar de forma conceitual:
  - Histórico de investimentos
  - Clareza da tese
  - Engajamento com startups
  - Áreas de valor agregado
- No protótipo, o score pode ser simulado.

#### Critérios de aceite
- Dado que acesso perfil de investidor, então vejo um indicador de reputação ou qualidade.
- Dado que o investidor tem tese completa e histórico, então o score pode aparecer mais alto.

---

## 16. Módulo de notificações

### US-039 — Receber notificações relevantes
**Como usuário**, quero receber notificações sobre interações importantes para não perder oportunidades.

#### Requisitos funcionais
- O sistema deve notificar quando:
  - Alguém curtir um post
  - Alguém comentar um post
  - Alguém seguir o usuário ou startup
  - Uma proposta for recebida
  - Uma mensagem for recebida
  - Um convite de startup for recebido
- No protótipo, as notificações podem ser exibidas em uma área visual.

#### Critérios de aceite
- Dado que recebo uma mensagem, então vejo uma notificação.
- Dado que recebo uma proposta, então a notificação deve indicar o tipo de proposta.
- Dado que clico na notificação, então devo ir para a área relacionada.

---

## 17. Módulo de impulsionamento e monetização visual

### US-040 — Visualizar opção de impulsionar post
**Como founder**, quero ver uma opção de impulsionar posts para aumentar a visibilidade de updates importantes.

#### Requisitos funcionais
- Posts devem ter ação visual “Impulsionar”.
- O protótipo deve simular planos ou opções de destaque.
- Não deve haver pagamento real.
- O impulsionamento deve ser apresentado como funcionalidade futura ou premium.

#### Critérios de aceite
- Dado que sou autor de um post, então vejo opção de impulsionar.
- Dado que clico em impulsionar, então vejo uma tela/modal explicando a funcionalidade premium.

---

### US-041 — Visualizar plano premium
**Como usuário**, quero visualizar opções premium para entender possíveis recursos pagos.

#### Requisitos funcionais
- O sistema deve exibir uma área de planos.
- Recursos premium simulados:
  - Impulsionar posts
  - Destaque no ranking
  - Analytics avançado
  - Propostas adicionais
  - Mais filtros de busca
- Não implementar pagamento real.

#### Critérios de aceite
- Dado que acesso a área premium, então vejo planos e benefícios simulados.
- Dado que clico em assinar, então o sistema deve mostrar aviso de funcionalidade não implementada no protótipo.

---

## 18. Módulo de analytics visual

### US-042 — Visualizar analytics da startup
**Como founder**, quero ver dados básicos de visibilidade da startup para entender o interesse gerado.

#### Requisitos funcionais
- O perfil da startup deve ter uma aba de analytics.
- Métricas visuais:
  - Visualizações do perfil
  - Novos seguidores
  - Interações em posts
  - Propostas recebidas
  - Posição em rankings
- No protótipo, os dados podem ser simulados.

#### Critérios de aceite
- Dado que sou membro autorizado da startup, então posso acessar analytics.
- Dado que visualizo analytics, então vejo cards com métricas simples.

---

### US-043 — Visualizar analytics de investidor
**Como investidor**, quero ver startups visualizadas, propostas recebidas e compatibilidade com minha tese.

#### Requisitos funcionais
- O perfil de investidor deve ter uma área de analytics.
- Métricas visuais:
  - Propostas recebidas
  - Startups salvas
  - Setores mais frequentes nas propostas
  - Compatibilidade média das propostas
- No protótipo, os dados podem ser simulados.

#### Critérios de aceite
- Dado que sou investidor, então vejo uma área de analytics.
- Dado que recebo propostas, então elas aparecem resumidas em analytics.

---

## 19. Módulo de salvos e pipeline

### US-044 — Salvar startups de interesse
**Como investidor**, quero salvar startups para acompanhar depois.

#### Requisitos funcionais
- O sistema deve permitir salvar startups.
- O investidor deve ter uma lista de startups salvas.
- Deve ser possível remover dos salvos.

#### Critérios de aceite
- Dado que clico em salvar startup, então ela aparece na minha lista de salvos.
- Dado que removo uma startup salva, então ela sai da lista.

---

### US-045 — Organizar startups em pipeline simples
**Como investidor**, quero organizar startups em um pipeline para acompanhar oportunidades.

#### Requisitos funcionais
- O sistema deve permitir mover startups salvas entre estágios:
  - Interessante
  - Em conversa
  - Avaliando
  - Arquivada
- O pipeline deve ser visual, estilo kanban simples.
- Não deve representar negociação financeira oficial dentro da plataforma.

#### Critérios de aceite
- Dado que salvo uma startup, então posso adicioná-la ao pipeline.
- Dado que movo uma startup para “Em conversa”, então o status deve ser atualizado.

---

## 20. Módulo de segurança, privacidade e limites do produto

### US-046 — Deixar claro que a plataforma não intermedia investimento financeiro
**Como plataforma**, quero deixar claro que conexões e propostas são informais para evitar interpretação de intermediação financeira.

#### Requisitos funcionais
- A plataforma deve exibir aviso em propostas de investimento.
- O aviso deve dizer que a plataforma não realiza transferência, investimento ou intermediação financeira.
- O sistema deve deixar claro que negociações formais acontecem fora da plataforma.

#### Critérios de aceite
- Dado que envio proposta de investimento, então vejo aviso de que a plataforma não intermedia investimento.
- Dado que acesso uma proposta, então o aviso deve estar visível.

---

### US-047 — Controlar visibilidade de informações sensíveis
**Como founder**, quero escolher quais informações da startup são públicas para proteger dados estratégicos.

#### Requisitos funcionais
- O sistema deve permitir marcar métricas como públicas ou privadas.
- Informações privadas só devem aparecer para membros autorizados no protótipo.
- O sistema deve indicar visualmente o status público/privado.

#### Critérios de aceite
- Dado que marco receita como privada, então visitantes não devem vê-la.
- Dado que sou Owner, então posso ver e editar essa informação.

---

## 21. Jornada principal do founder

1. Criar conta.
2. Criar perfil pessoal.
3. Criar ou vincular startup.
4. Completar perfil da startup.
5. Publicar updates estruturados e posts livres.
6. Acompanhar ranking e analytics.
7. Buscar investidores compatíveis.
8. Enviar mensagens ou propostas.
9. Buscar co-founders ou advisors.
10. Gerenciar propostas e conversas.

## 22. Jornada principal do investidor

1. Criar conta.
2. Criar perfil pessoal.
3. Configurar perfil de investidor.
4. Definir tese de investimento.
5. Explorar feed e rankings.
6. Buscar startups por filtros.
7. Salvar startups interessantes.
8. Organizar pipeline.
9. Receber propostas estruturadas.
10. Conversar com founders fora ou dentro da plataforma.

## 23. Jornada principal de co-founder

1. Criar conta.
2. Criar perfil pessoal.
3. Marcar interesse em ser co-founder.
4. Informar skills e disponibilidade.
5. Buscar startups que procuram co-founder.
6. Receber propostas.
7. Conversar com founders.

## 24. Prioridade sugerida para MVP

### MVP essencial
- Cadastro e login
- Perfil pessoal
- Perfil de startup
- Relação usuário-startup
- Feed com posts livres
- Updates estruturados
- Ranking de performance e popularidade com dados simulados
- Busca de startups
- Busca de investidores
- Perfil de investidor
- Mensagens diretas
- Propostas estruturadas
- Co-founder matching básico

### Versão 2
- Trending ranking
- Badges automáticos
- Pipeline de investidores
- Analytics visual
- Score de founder, startup e investidor
- Controle avançado de privacidade

### Futuro
- Algoritmo real de matching
- Validação de investidores
- Reputação baseada em histórico real
- Integrações externas
- Monetização real

## 25. Prompt base para orientar uma IA de desenvolvimento

Use este documento como fonte de verdade do produto.

Antes de implementar qualquer funcionalidade:
1. Identifique a história de usuário relacionada.
2. Liste os requisitos funcionais envolvidos.
3. Explique quais telas ou componentes serão alterados.
4. Implemente apenas uma funcionalidade por vez.
5. Não altere áreas não relacionadas.
6. Após implementar, informe como testar manualmente.

Priorize clareza, protótipo navegável e experiência de produto. Não implemente pagamentos reais, investimentos reais ou integrações financeiras nesta fase.

