package com.conninvest.backend.config;

import com.conninvest.backend.model.*;
import com.conninvest.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepo,
            StartupRepository startupRepo,
            PostRepository postRepo,
            StartupMembershipRepository membershipRepo,
            MessageRepository messageRepo,
            NotificationRepository notifRepo,
            FollowRepository followRepo,
            NicheConfigRepository nicheRepo,
            CapTableEntryRepository capTableRepo,
            StartupRiskRepository riskRepo,
            StartupNicheDataRepository nicheDataRepo) {
        return args -> {
            if (userRepo.count() > 0) return;

            // --- Nichos ---
            nicheRepo.save(niche("agtech", "AgTech", 1,
                "[{\"key\":\"hectares_cobertos\",\"label\":\"Hectares cobertos\",\"placeholder\":\"Ex: 50.000 ha\"}," +
                "{\"key\":\"produtores_ativos\",\"label\":\"Produtores ativos\",\"placeholder\":\"Ex: 1.200\"}," +
                "{\"key\":\"safra_impactada\",\"label\":\"Safra impactada\",\"placeholder\":\"Ex: Soja, Milho\"}," +
                "{\"key\":\"reducao_insumos\",\"label\":\"Redução de insumos\",\"placeholder\":\"Ex: 15%\"}," +
                "{\"key\":\"culturas_atendidas\",\"label\":\"Culturas atendidas\",\"placeholder\":\"Ex: Soja, Milho, Café\"}," +
                "{\"key\":\"cooperativas\",\"label\":\"Cooperativas parceiras\",\"placeholder\":\"Ex: 8\"}]"));

            nicheRepo.save(niche("fintech", "Fintech", 2,
                "[{\"key\":\"tpv\",\"label\":\"TPV mensal\",\"placeholder\":\"Ex: R$ 50M\"}," +
                "{\"key\":\"contas_ativas\",\"label\":\"Contas ativas\",\"placeholder\":\"Ex: 12.000\"}," +
                "{\"key\":\"inadimplencia\",\"label\":\"Inadimplência\",\"placeholder\":\"Ex: 2.1%\"}," +
                "{\"key\":\"custo_por_transacao\",\"label\":\"Custo por transação\",\"placeholder\":\"Ex: R$ 0,45\"}," +
                "{\"key\":\"autorizacoes_regulatorias\",\"label\":\"Autorizações regulatórias\",\"placeholder\":\"Ex: Banco Central\"}," +
                "{\"key\":\"aprovacao_credito\",\"label\":\"Taxa de aprovação de crédito\",\"placeholder\":\"Ex: 68%\"}," +
                "{\"key\":\"exposicao_credito\",\"label\":\"Exposição em crédito\",\"placeholder\":\"Ex: R$ 2M\"}]"));

            nicheRepo.save(niche("healthtech", "Healthtech", 3,
                "[{\"key\":\"hospitais_integrados\",\"label\":\"Hospitais integrados\",\"placeholder\":\"Ex: 45\"}," +
                "{\"key\":\"pacientes\",\"label\":\"Pacientes atendidos\",\"placeholder\":\"Ex: 120.000\"}," +
                "{\"key\":\"certificacoes\",\"label\":\"Certificações\",\"placeholder\":\"Ex: ANVISA, ISO 13485\"}," +
                "{\"key\":\"reducao_diagnostico\",\"label\":\"Redução no tempo de diagnóstico\",\"placeholder\":\"Ex: 40%\"}," +
                "{\"key\":\"tipo_contrato\",\"label\":\"Tipo de contrato\",\"placeholder\":\"Ex: SaaS, por exame\"}]"));

            nicheRepo.save(niche("saas_b2b", "SaaS B2B", 4,
                "[{\"key\":\"nrr\",\"label\":\"NRR (Net Revenue Retention)\",\"placeholder\":\"Ex: 115%\"}," +
                "{\"key\":\"churn_rate\",\"label\":\"Churn Rate\",\"placeholder\":\"Ex: 1.8%\"}," +
                "{\"key\":\"acv\",\"label\":\"ACV (Annual Contract Value)\",\"placeholder\":\"Ex: R$ 24k\"}," +
                "{\"key\":\"expansao_receita\",\"label\":\"Expansão de receita\",\"placeholder\":\"Ex: 22%\"}," +
                "{\"key\":\"sales_cycle\",\"label\":\"Sales Cycle\",\"placeholder\":\"Ex: 45 dias\"}," +
                "{\"key\":\"composicao_leads\",\"label\":\"Composição de leads\",\"placeholder\":\"Ex: 60% inbound\"}]"));

            nicheRepo.save(niche("edtech", "Edtech", 5,
                "[{\"key\":\"alunos_ativos\",\"label\":\"Alunos ativos\",\"placeholder\":\"Ex: 5.000\"}," +
                "{\"key\":\"conclusao\",\"label\":\"Taxa de conclusão\",\"placeholder\":\"Ex: 72%\"}," +
                "{\"key\":\"nps\",\"label\":\"NPS\",\"placeholder\":\"Ex: 68\"}," +
                "{\"key\":\"contratos_b2b\",\"label\":\"Contratos B2B\",\"placeholder\":\"Ex: 12 escolas\"}," +
                "{\"key\":\"metricas_aprendizagem\",\"label\":\"Métricas de aprendizagem\",\"placeholder\":\"Ex: Nota média +18%\"}]"));

            nicheRepo.save(niche("outros", "Outros", 6,
                "[{\"key\":\"nicho_livre\",\"label\":\"Nicho livre\",\"placeholder\":\"Descreva o nicho\"}," +
                "{\"key\":\"metricas_customizadas\",\"label\":\"Métricas customizadas\",\"placeholder\":\"Ex: Pedidos/dia: 1.200\"}," +
                "{\"key\":\"diferenciais_tecnicos\",\"label\":\"Diferenciais técnicos\",\"placeholder\":\"Ex: Patente, algoritmo proprietário\"}," +
                "{\"key\":\"parcerias_estrategicas\",\"label\":\"Parcerias estratégicas\",\"placeholder\":\"Ex: Amazon AWS, Google\"}]"));

            // --- Users ---
            User mariana = user("Mariana Silva", "mariana@founder.com", "123456",
                List.of("Founder"), "Empreendedora apaixonada por tecnologia e inovação. Buscando revolucionar o mercado financeiro com IA.",
                "São Paulo, SP", "https://i.pravatar.cc/150?img=5",
                "Produto, Estratégia, Captação", "Fintech, Open Finance, B2B SaaS",
                "Fundadora, TechNova (2020-Presente) | Product Manager, FinCorp (2016-2020)");
            mariana.setSeekingCoFounder(true);
            mariana.setDesiredCoFounderType("CTO com experiência em IA aplicada");
            mariana.setCoFounderArea("tech");
            mariana.setCoFounderDedication("part-time");
            mariana.setCoFounderDescription("Procuro uma pessoa técnica para dividir decisões de produto, arquitetura e evolução da plataforma.");
            userRepo.save(mariana);

            User julia = user("Julia Mendes", "julia@dev.com", "123456",
                List.of("Founder", "Buscando co-founder"), "Tech co-founder. Engenheira full-stack apaixonada por healthtech e AI.",
                "Florianópolis, SC", "https://i.pravatar.cc/150?img=1",
                "Backend, AI, Cloud", "Healthtech, AI, SaaS",
                "Engenheira Sênior, MedTech (2019-Presente) | Dev Lead, CloudSoft (2016-2019)");
            julia.setSeekingCoFounder(true);
            julia.setCoFounderArea("business");
            julia.setCoFounderDedication("full-time");
            julia.setCoFounderDescription("Tenho o produto, preciso de um co-founder comercial para liderar go-to-market.");
            userRepo.save(julia);

            User camila = user("Camila Rocha", "camila@design.com", "123456",
                List.of("Founder", "Buscando co-founder"), "Designer e product builder. Criando experiências que encantam.",
                "Belo Horizonte, MG", "https://i.pravatar.cc/150?img=9",
                "Design, UX, Produto", "Edtech, Consumer Apps",
                "Head of Design, EduTech (2021-Presente) | UX Lead, Agência Digital (2018-2021)");
            camila.setSeekingCoFounder(true);
            camila.setCoFounderArea("tech");
            camila.setCoFounderDedication("full-time");
            camila.setCoFounderDescription("Buscando co-founder técnico para construir minha plataforma de aprendizado adaptativo.");
            userRepo.save(camila);

            User pedro = user("Pedro Almeida", "pedro@cto.com", "123456",
                List.of("Founder"), "CTO serial. Já construí 3 produtos do zero até scale.",
                "Rio de Janeiro, RJ", "https://i.pravatar.cc/150?img=32",
                "Arquitetura, Infraestrutura, Liderança técnica", "AgriTech, Logística, B2B",
                "CTO, AgroNova (2022-Presente) | Engenheiro Principal, DataFlow (2018-2022)");
            userRepo.save(pedro);

            User rafael = user("Rafael Costa", "rafael@angel.com", "123456",
                List.of("Investidor"), "Angel investor · 30+ investimentos em early stage. Foco em B2B SaaS e Fintech.",
                "São Paulo, SP", "https://i.pravatar.cc/150?img=12",
                "Fundraising, GTM, Network", "B2B SaaS, Fintech, PropTech",
                "Partner, AngelHub BR (2018-Presente) | Ex-fundador TechVision (exit 2017)");
            rafael.setInvestorType("angel");
            rafael.setSectorsOfInterest("Fintech, B2B SaaS, PropTech");
            rafael.setStagesOfInterest("Pre-seed, Seed");
            rafael.setAverageTicket("R$ 100k – R$ 500k");
            rafael.setValueAdd("Network, Fundraising, GTM Strategy");
            rafael.setOperationStyle("hands-on");
            rafael.setContactPreference("Propostas estruturadas com métricas");
            rafael.setInvestmentHistory("FinPulse (2023), TechVision exit (2017), 3 outras startups B2B");
            userRepo.save(rafael);

            User ana = user("Ana Beatriz Lima", "ana@vc.com", "123456",
                List.of("Investidor"), "Partner em VC focada em deep tech e healthtech. Mais de 15 anos investindo em inovação.",
                "São Paulo, SP", "https://i.pravatar.cc/150?img=44",
                "Estratégia, M&A, Portfolio Management", "Healthtech, DeepTech, BioTech",
                "Partner, Vox Capital (2015-Presente) | Analista, BTG Pactual (2010-2015)");
            ana.setInvestorType("VC");
            ana.setSectorsOfInterest("Healthtech, DeepTech, BioTech");
            ana.setStagesOfInterest("Seed, Series A");
            ana.setAverageTicket("R$ 1M – R$ 5M");
            ana.setValueAdd("Network internacional, Due diligence, Board seat");
            ana.setOperationStyle("híbrido");
            ana.setContactPreference("Deck executivo + reunião exploratória");
            userRepo.save(ana);

            User marcos = user("Marcos Oliveira", "marcos@advisor.com", "123456",
                List.of("Advisor"), "Go-to-market specialist. Ajudei +20 startups B2B a atingir product-market fit.",
                "Curitiba, PR", "https://i.pravatar.cc/150?img=52",
                "Vendas, Growth, GTM", "B2B, Enterprise SaaS",
                "Advisor independente (2020-Presente) | VP Sales, RD Station (2015-2020)");
            marcos.setInvestorType("advisor");
            marcos.setSectorsOfInterest("B2B, Enterprise SaaS");
            marcos.setValueAdd("GTM, Vendas B2B, Rede de CXOs");
            marcos.setOperationStyle("hands-on");
            userRepo.save(marcos);

            // --- Startups ---
            Startup finpulse = new Startup();
            finpulse.setName("FinPulse");
            finpulse.setSector("Fintech");
            finpulse.setShortDescription("Tesouraria B2B com IA preditiva.");
            finpulse.setPitch("Tesouraria B2B com IA preditiva.");
            finpulse.setDescription("Plataforma de otimização de tesouraria B2B utilizando inteligência artificial preditiva.");
            finpulse.setFullDescription("A FinPulse ajuda empresas B2B a prever caixa, conciliar dados financeiros e tomar decisões de tesouraria com mais velocidade e precisão.");
            finpulse.setProblemDescription("Empresas de médio porte perdem em média R$ 280k/ano por má gestão de tesouraria e falta de previsibilidade de caixa.");
            finpulse.setSolutionDescription("Plataforma SaaS com IA preditiva que automatiza conciliação bancária, prevê fluxo de caixa com 93% de acurácia e sugere otimizações em tempo real.");
            finpulse.setCompetitiveDifferential("Motor de IA proprietário treinado com dados de mais de 10.000 empresas brasileiras. Integração nativa com os principais ERPs do mercado.");
            finpulse.setStage("Tração");
            finpulse.setLocation("São Paulo, SP");
            finpulse.setWebsiteUrl("https://finpulse.com.br");
            finpulse.setFoundingYear(2021);
            finpulse.setTam("R$ 18B");
            finpulse.setSam("R$ 3.2B");
            finpulse.setSom("R$ 320M");
            finpulse.setMarketSource("IBGE + Relatório Fintechlab 2024");
            finpulse.setMarketTiming("Crescimento de 45% no mercado de fintechs B2B em 2023. Open Finance abrindo novos canais de distribuição.");
            finpulse.setMonthlyRevenue("R$ 180k");
            finpulse.setAnnualRevenue("R$ 1.8M");
            finpulse.setMrr("R$ 180k");
            finpulse.setArr("R$ 2.16M");
            finpulse.setCac("R$ 4.200");
            finpulse.setLtv("R$ 38.000");
            finpulse.setGrossMargin("72%");
            finpulse.setBurnRate("R$ 95k/mês");
            finpulse.setRunway("18 meses");
            finpulse.setUsersCount(1200);
            finpulse.setGrowthPercent("+32%");
            finpulse.setClientsCount(42);
            finpulse.setChurn("2.1%");
            finpulse.setMetricsPublic(true);
            finpulse.setRoundStatus("Aberta");
            finpulse.setRoundAmountRaised("R$ 180k captados");
            finpulse.setRoundValuation("R$ 9M pre-money");
            finpulse.setRoundStructure("SAFE + equity");
            finpulse.setRoundPercentCommitted("12%");
            finpulse.setRoundCapitalUse("60% produto e engenharia, 30% go-to-market, 10% operações");
            finpulse.setCurrentObjective("Captar R$ 1.5M Seed");
            finpulse.setBadges("Top Growth");
            finpulse.setSelectedNiches(List.of("fintech", "saas_b2b"));
            finpulse.setMainMetrics("Receita: R$ 180k | Usuários: 1.200 | Crescimento: +32%");
            startupRepo.save(finpulse);

            Startup agronova = new Startup();
            agronova.setName("AgroNova");
            agronova.setSector("AgriTech");
            agronova.setShortDescription("Gestão de fazendas com IoT e IA.");
            agronova.setPitch("Gestão de fazendas com IoT e IA.");
            agronova.setDescription("Plataforma de gestão inteligente para fazendas usando IoT e análise de dados.");
            agronova.setFullDescription("AgroNova conecta sensores de campo a um dashboard de gestão, permitindo decisões baseadas em dados sobre irrigação, fertilização e colheita.");
            agronova.setProblemDescription("Produtores rurais tomam decisões críticas de irrigação e fertilização no escuro, perdendo até 20% da produtividade por decisões subótimas.");
            agronova.setSolutionDescription("Rede de sensores IoT + plataforma de análise preditiva que orienta decisões agronômicas em tempo real, reduzindo insumos e aumentando produtividade.");
            agronova.setCompetitiveDifferential("Hardware proprietário com 5 anos de dados históricos de mais de 3.000 fazendas. Único com previsão de colheita com 15 dias de antecedência.");
            agronova.setStage("Escala");
            agronova.setLocation("Ribeirão Preto, SP");
            agronova.setWebsiteUrl("https://agronova.agr.br");
            agronova.setFoundingYear(2019);
            agronova.setTam("R$ 42B");
            agronova.setSam("R$ 8.5B");
            agronova.setSom("R$ 850M");
            agronova.setMarketSource("MAPA + CNA 2024");
            agronova.setMarketTiming("Brasil é o maior exportador de alimentos do mundo. Digitalização do agro crescendo 38% ao ano.");
            agronova.setMonthlyRevenue("R$ 420k");
            agronova.setAnnualRevenue("R$ 4.8M");
            agronova.setMrr("R$ 420k");
            agronova.setArr("R$ 5.04M");
            agronova.setCac("R$ 12.000");
            agronova.setLtv("R$ 85.000");
            agronova.setGrossMargin("58%");
            agronova.setBurnRate("R$ 220k/mês");
            agronova.setRunway("22 meses");
            agronova.setUsersCount(3200);
            agronova.setGrowthPercent("+18%");
            agronova.setClientsCount(156);
            agronova.setChurn("1.4%");
            agronova.setMetricsPublic(true);
            agronova.setRoundStatus("Fechada");
            agronova.setRoundAmountRaised("R$ 8M Series A");
            agronova.setRoundValuation("R$ 42M post-money");
            agronova.setCurrentObjective("Expansão para mercado Latino-Americano");
            agronova.setBadges("Most Consistent");
            agronova.setSelectedNiches(List.of("agtech"));
            agronova.setMainMetrics("Receita: R$ 420k | Usuários: 3.200 | Crescimento: +18%");
            startupRepo.save(agronova);

            Startup vitalia = new Startup();
            vitalia.setName("Vitalia");
            vitalia.setSector("Healthtech");
            vitalia.setShortDescription("Prontuário eletrônico para clínicas independentes.");
            vitalia.setPitch("Prontuário eletrônico para clínicas independentes.");
            vitalia.setDescription("Sistema de prontuário eletrônico simplificado para clínicas de pequeno e médio porte.");
            vitalia.setFullDescription("Vitalia torna o prontuário eletrônico acessível para clínicas independentes, com interface simples e integração com planos de saúde.");
            vitalia.setProblemDescription("80% das clínicas independentes ainda usam papel ou sistemas ultrapassados, gerando erros médicos e ineficiência.");
            vitalia.setSolutionDescription("SaaS de prontuário eletrônico com UX simplificado, IA para sugestões clínicas e integração com ANS e TISS.");
            vitalia.setCompetitiveDifferential("Único sistema com onboarding em 48h e preço acessível (R$ 290/mês). Integração com +12 planos de saúde.");
            vitalia.setStage("MVP");
            vitalia.setLocation("Belo Horizonte, MG");
            vitalia.setFoundingYear(2023);
            vitalia.setTam("R$ 5.2B");
            vitalia.setSam("R$ 1.1B");
            vitalia.setSom("R$ 88M");
            vitalia.setMarketSource("ANS + Relatório Saúde Digital 2024");
            vitalia.setMonthlyRevenue("R$ 28k");
            vitalia.setMrr("R$ 28k");
            vitalia.setCac("R$ 1.800");
            vitalia.setLtv("R$ 12.000");
            vitalia.setGrossMargin("81%");
            vitalia.setBurnRate("R$ 32k/mês");
            vitalia.setRunway("9 meses");
            vitalia.setUsersCount(320);
            vitalia.setGrowthPercent("+24%");
            vitalia.setClientsCount(18);
            vitalia.setChurn("3.8%");
            vitalia.setMetricsPublic(true);
            vitalia.setRoundStatus("Buscando");
            vitalia.setRoundValuation("R$ 2.5M pre-money");
            vitalia.setRoundCapitalUse("70% produto, 20% vendas, 10% certificações");
            vitalia.setCurrentObjective("Atingir 50 clínicas pagantes");
            vitalia.setBadges("Rising");
            vitalia.setSelectedNiches(List.of("healthtech", "saas_b2b"));
            vitalia.setMainMetrics("Receita: R$ 28k | Usuários: 320 | Crescimento: +24%");
            startupRepo.save(vitalia);

            Startup lumen = new Startup();
            lumen.setName("Lumen");
            lumen.setSector("Edtech");
            lumen.setShortDescription("Aprendizado adaptativo com IA para o ensino básico.");
            lumen.setPitch("Aprendizado adaptativo com IA para o ensino básico.");
            lumen.setDescription("Plataforma de educação adaptativa que usa IA para personalizar o aprendizado.");
            lumen.setFullDescription("Lumen identifica lacunas no aprendizado de cada aluno e cria trilhas personalizadas com exercícios, vídeos e gamificação.");
            lumen.setProblemDescription("Professores com 30+ alunos não conseguem personalizar o ensino. 40% dos alunos chegam ao 5º ano sem saber ler adequadamente.");
            lumen.setSolutionDescription("Plataforma adaptativa com IA que ajusta dificuldade e formato em tempo real, gerando relatórios para professores e pais.");
            lumen.setCompetitiveDifferential("Algoritmo de personalização treinado com dados de 50.000 alunos brasileiros. Alinhado à BNCC e com gamificação comprovada.");
            lumen.setStage("Primeiros usuários");
            lumen.setLocation("Florianópolis, SC");
            lumen.setFoundingYear(2022);
            lumen.setTam("R$ 15B");
            lumen.setSam("R$ 2.8B");
            lumen.setSom("R$ 140M");
            lumen.setMarketSource("INEP + EdTech Report Brasil 2024");
            lumen.setMonthlyRevenue("R$ 8k");
            lumen.setMrr("R$ 8k");
            lumen.setCac("R$ 420");
            lumen.setLtv("R$ 3.200");
            lumen.setGrossMargin("88%");
            lumen.setBurnRate("R$ 18k/mês");
            lumen.setRunway("14 meses");
            lumen.setUsersCount(890);
            lumen.setGrowthPercent("+45%");
            lumen.setClientsCount(4);
            lumen.setChurn("5.2%");
            lumen.setMetricsPublic(true);
            lumen.setRoundStatus("Buscando");
            lumen.setRoundValuation("R$ 4M pre-money");
            lumen.setRoundCapitalUse("50% produto, 30% parcerias com escolas, 20% marketing");
            lumen.setCurrentObjective("Fechar parceria com 5 escolas públicas");
            lumen.setBadges("Trending");
            lumen.setSelectedNiches(List.of("edtech"));
            lumen.setMainMetrics("Receita: R$ 8k | Usuários: 890 | Crescimento: +45%");
            startupRepo.save(lumen);

            // --- Memberships ---
            membership(mariana.getId(), finpulse.getId(), "Owner", membershipRepo);
            membership(julia.getId(), vitalia.getId(), "Owner", membershipRepo);
            membership(camila.getId(), lumen.getId(), "Owner", membershipRepo);
            membership(pedro.getId(), agronova.getId(), "Owner", membershipRepo);
            membership(marcos.getId(), finpulse.getId(), "Advisor", membershipRepo);

            // --- Cap Table ---
            capTableRepo.save(capEntry(finpulse.getId(), "Mariana Silva", "Founder", 45.0, 0));
            capTableRepo.save(capEntry(finpulse.getId(), "Co-founder Técnico", "Co-founder", 25.0, 1));
            capTableRepo.save(capEntry(finpulse.getId(), "AngelHub BR", "Investidor", 15.0, 2));
            capTableRepo.save(capEntry(finpulse.getId(), "Pool de opções", "ESOP", 15.0, 3));

            capTableRepo.save(capEntry(agronova.getId(), "Pedro Almeida", "Founder", 38.0, 0));
            capTableRepo.save(capEntry(agronova.getId(), "Co-founder Agro", "Co-founder", 22.0, 1));
            capTableRepo.save(capEntry(agronova.getId(), "Fundo Agro Ventures", "Investidor", 25.0, 2));
            capTableRepo.save(capEntry(agronova.getId(), "Pool de opções", "ESOP", 15.0, 3));

            capTableRepo.save(capEntry(vitalia.getId(), "Julia Mendes", "Founder", 70.0, 0));
            capTableRepo.save(capEntry(vitalia.getId(), "Anjos BH", "Investidor", 10.0, 1));
            capTableRepo.save(capEntry(vitalia.getId(), "Pool de opções", "ESOP", 20.0, 2));

            capTableRepo.save(capEntry(lumen.getId(), "Camila Rocha", "Founder", 75.0, 0));
            capTableRepo.save(capEntry(lumen.getId(), "Pool de opções", "ESOP", 25.0, 1));

            // --- Riscos ---
            riskRepo.save(risk(finpulse.getId(), "Concentração de clientes", "medio",
                "Top 3 clientes = 38% da receita. Plano de diversificação com outbound focado em PMEs.", 0));
            riskRepo.save(risk(finpulse.getId(), "Regulatório Open Finance", "baixo",
                "Acompanhamento próximo do Banco Central. Arquitetura já preparada para novas fases.", 1));
            riskRepo.save(risk(finpulse.getId(), "Churn acima do projetado", "alto",
                "Customer Success dedicado após 90 dias. NPS medido mensalmente.", 2));

            riskRepo.save(risk(agronova.getId(), "Dependência climática", "medio",
                "Diversificação geográfica (5 estados). Modelo de preço por resultado reduz impacto.", 0));
            riskRepo.save(risk(agronova.getId(), "Custo de hardware", "baixo",
                "Nova geração de sensores reduz custo 40%. Parceria com fabricante nacional.", 1));

            riskRepo.save(risk(vitalia.getId(), "Adoção lenta por médicos", "alto",
                "Onboarding simplificado + treinamento gratuito + suporte WhatsApp.", 0));
            riskRepo.save(risk(vitalia.getId(), "LGPD e dados de saúde", "medio",
                "DPO contratado. Dados criptografados e armazenados no Brasil.", 1));

            riskRepo.save(risk(lumen.getId(), "Acesso a escolas públicas", "alto",
                "Parceria com Secretaria de Educação de SC em andamento.", 0));
            riskRepo.save(risk(lumen.getId(), "Monetização B2C lenta", "medio",
                "Pivô para B2B (escolas) em andamento. Meta de 5 contratos em 90 dias.", 1));

            // --- Niche Data ---
            nicheDataRepo.save(nicheData(finpulse.getId(), "fintech",
                "{\"tpv\":\"R$ 12M/mês\",\"contas_ativas\":\"1.200\",\"inadimplencia\":\"2.1%\",\"custo_por_transacao\":\"R$ 0,32\",\"autorizacoes_regulatorias\":\"BACEN - regulado\"}"));
            nicheDataRepo.save(nicheData(finpulse.getId(), "saas_b2b",
                "{\"nrr\":\"118%\",\"churn_rate\":\"2.1%\",\"acv\":\"R$ 51.4k\",\"expansao_receita\":\"18%\",\"sales_cycle\":\"38 dias\",\"composicao_leads\":\"65% inbound\"}"));

            nicheDataRepo.save(nicheData(agronova.getId(), "agtech",
                "{\"hectares_cobertos\":\"182.000 ha\",\"produtores_ativos\":\"3.200\",\"safra_impactada\":\"Soja, Milho, Cana\",\"reducao_insumos\":\"19%\",\"culturas_atendidas\":\"Soja, Milho, Cana, Café\",\"cooperativas\":\"12\"}"));

            nicheDataRepo.save(nicheData(vitalia.getId(), "healthtech",
                "{\"hospitais_integrados\":\"18 clínicas\",\"pacientes\":\"4.200\",\"certificacoes\":\"CFM compatível\",\"reducao_diagnostico\":\"32%\",\"tipo_contrato\":\"SaaS mensal\"}"));
            nicheDataRepo.save(nicheData(vitalia.getId(), "saas_b2b",
                "{\"nrr\":\"104%\",\"churn_rate\":\"3.8%\",\"acv\":\"R$ 3.5k\",\"sales_cycle\":\"12 dias\",\"composicao_leads\":\"80% indicação\"}"));

            nicheDataRepo.save(nicheData(lumen.getId(), "edtech",
                "{\"alunos_ativos\":\"890\",\"conclusao\":\"68%\",\"nps\":\"72\",\"contratos_b2b\":\"4 escolas\",\"metricas_aprendizagem\":\"Nota média +22%\"}"));

            // --- Follows ---
            follow(mariana.getId(), "STARTUP", agronova.getId(), followRepo);
            follow(mariana.getId(), "STARTUP", lumen.getId(), followRepo);
            follow(julia.getId(), "STARTUP", finpulse.getId(), followRepo);
            follow(rafael.getId(), "STARTUP", finpulse.getId(), followRepo);
            follow(rafael.getId(), "STARTUP", agronova.getId(), followRepo);
            follow(ana.getId(), "STARTUP", vitalia.getId(), followRepo);
            follow(marcos.getId(), "STARTUP", finpulse.getId(), followRepo);

            // --- Posts ---
            Post p1 = new Post();
            p1.setAuthorId(finpulse.getId()); p1.setStartupId(finpulse.getId());
            p1.setAuthorName("FinPulse"); p1.setAuthorType("Startup"); p1.setAuthorPhoto("F");
            p1.setType("STRUCTURED_UPDATE"); p1.setBadge("Update");
            p1.getMetrics().add(new Metric("RECEITA MRR", "R$ 136k", "R$ 180k"));
            p1.getMetrics().add(new Metric("USUÁRIOS ATIVOS", "920", "1.200"));
            p1.setContent("Fechamos o melhor mês da nossa história! Crescimento puxado pelo novo módulo de conciliação automática.");
            p1.setLikesCount(142); p1.setCommentsCount(28);
            postRepo.save(p1);

            Post p2 = new Post();
            p2.setAuthorId(julia.getId()); p2.setAuthorName("Julia Mendes");
            p2.setAuthorType("Founder"); p2.setAuthorPhoto("https://i.pravatar.cc/150?img=1");
            p2.setType("FREE_TEXT");
            p2.setContent("Bastidores do nosso último sprint: 5 dias de hackathon interno para acelerar o roadmap de IA. Equipe incrível, produto evoluindo rápido.");
            p2.setLikesCount(89); p2.setCommentsCount(12);
            postRepo.save(p2);

            Post p3 = new Post();
            p3.setAuthorId(agronova.getId()); p3.setStartupId(agronova.getId());
            p3.setAuthorName("AgroNova"); p3.setAuthorType("Startup"); p3.setAuthorPhoto("A");
            p3.setType("STRUCTURED_UPDATE"); p3.setBadge("Crescimento");
            p3.getMetrics().add(new Metric("CLIENTES", "98", "156"));
            p3.getMetrics().add(new Metric("MRR", "R$ 310k", "R$ 420k"));
            p3.setContent("Expansão acelerada: fechamos 58 novos clientes só no último trimestre.");
            p3.setLikesCount(203); p3.setCommentsCount(41);
            postRepo.save(p3);

            Post p4 = new Post();
            p4.setAuthorId(lumen.getId()); p4.setStartupId(lumen.getId());
            p4.setAuthorName("Lumen"); p4.setAuthorType("Startup"); p4.setAuthorPhoto("L");
            p4.setType("FREE_TEXT");
            p4.setContent("Chegamos a 890 alunos usando a plataforma! Estamos vendo crianças de 8 anos completando módulos de matemática avançada de forma divertida.");
            p4.setLikesCount(67); p4.setCommentsCount(9);
            postRepo.save(p4);

            Post p5 = new Post();
            p5.setAuthorId(mariana.getId()); p5.setAuthorName("Mariana Silva");
            p5.setAuthorType("Founder"); p5.setAuthorPhoto("https://i.pravatar.cc/150?img=5");
            p5.setType("FREE_TEXT");
            p5.setContent("Acabo de voltar do maior evento de fintech da América Latina. O mercado está faminto por soluções de tesouraria inteligente para médias empresas.");
            p5.setLikesCount(54); p5.setCommentsCount(7);
            postRepo.save(p5);

            // --- Messages ---
            msg(rafael, mariana, "Oi Mariana, adorei ver os números da FinPulse. Podemos conversar?", messageRepo);
            msg(mariana, rafael, "Oi Rafael! Claro, fico feliz com o interesse. Quando você tem disponibilidade?", messageRepo);
            msg(rafael, mariana, "Que tal quinta-feira às 14h?", messageRepo);

            // --- Notifications ---
            notif(mariana.getId(), "FOLLOW", "Rafael Costa começou a seguir a FinPulse", "Rafael Costa · Investidor", rafael.getId(), "user", notifRepo);
            notif(mariana.getId(), "MESSAGE", "Nova mensagem de Rafael Costa", "Oi Mariana, adorei ver os números da FinPulse...", rafael.getId(), "user", notifRepo);
        };
    }

    private User user(String name, String email, String pass, List<String> types, String bio, String location,
                      String photo, String skills, String interests, String experience) {
        User u = new User();
        u.setName(name); u.setEmail(email); u.setPassword(pass);
        u.setProfileTypes(types); u.setBio(bio); u.setLocation(location);
        u.setPhoto(photo); u.setMainSkills(skills); u.setInterests(interests);
        u.setPastExperiences(experience);
        return u;
    }

    private NicheConfig niche(String key, String label, int order, String fieldsJson) {
        return new NicheConfig(key, label, order, fieldsJson);
    }

    private CapTableEntry capEntry(Long startupId, String name, String type, Double pct, int order) {
        CapTableEntry e = new CapTableEntry();
        e.setStartupId(startupId); e.setName(name); e.setType(type);
        e.setPercentage(pct); e.setSortOrder(order);
        return e;
    }

    private StartupRisk risk(Long startupId, String title, String level, String mitigation, int order) {
        StartupRisk r = new StartupRisk();
        r.setStartupId(startupId); r.setTitle(title); r.setLevel(level);
        r.setMitigation(mitigation); r.setSortOrder(order);
        return r;
    }

    private StartupNicheData nicheData(Long startupId, String nicheKey, String json) {
        StartupNicheData d = new StartupNicheData();
        d.setStartupId(startupId); d.setNicheKey(nicheKey); d.setFieldValuesJson(json);
        return d;
    }

    private void membership(Long userId, Long startupId, String role, StartupMembershipRepository repo) {
        StartupMembership m = new StartupMembership();
        m.setUserId(userId); m.setStartupId(startupId); m.setRole(role);
        repo.save(m);
    }

    private void follow(Long followerId, String type, Long targetId, FollowRepository repo) {
        Follow f = new Follow();
        f.setFollowerId(followerId); f.setTargetType(type); f.setTargetId(targetId);
        repo.save(f);
    }

    private void msg(User from, User to, String content, MessageRepository repo) {
        Message m = new Message();
        m.setSenderId(from.getId()); m.setSenderName(from.getName()); m.setSenderPhoto(from.getPhoto());
        m.setReceiverId(to.getId()); m.setReceiverName(to.getName()); m.setReceiverPhoto(to.getPhoto());
        m.setContent(content);
        repo.save(m);
    }

    private void notif(Long recipientId, String type, String title, String body, Long relatedId, String relatedType, NotificationRepository repo) {
        Notification n = new Notification();
        n.setRecipientId(recipientId); n.setType(type); n.setTitle(title);
        n.setBody(body); n.setRelatedId(relatedId); n.setRelatedType(relatedType);
        repo.save(n);
    }
}
