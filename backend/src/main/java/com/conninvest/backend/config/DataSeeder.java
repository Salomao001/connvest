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
            FollowRepository followRepo) {
        return args -> {
            if (userRepo.count() > 0) return;

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
            Startup finpulse = startup("FinPulse", "Fintech",
                "Tesouraria B2B com IA preditiva.",
                "Plataforma de otimização de tesouraria B2B utilizando inteligência artificial preditiva.",
                "A FinPulse ajuda empresas B2B a prever caixa, conciliar dados financeiros e tomar decisões de tesouraria com mais velocidade e precisão.",
                "Tração", "São Paulo, SP",
                "R$ 180k", 1200, "+32%", 42, "R$ 180k", "2.1%",
                "Captar R$ 1.5M Seed", "Top Growth");
            startupRepo.save(finpulse);

            Startup agronova = startup("AgroNova", "AgriTech",
                "Gestão de fazendas com IoT e IA.",
                "Plataforma de gestão inteligente para fazendas usando IoT e análise de dados.",
                "AgroNova conecta sensores de campo a um dashboard de gestão, permitindo decisões baseadas em dados sobre irrigação, fertilização e colheita.",
                "Escala", "Ribeirão Preto, SP",
                "R$ 420k", 3200, "+18%", 156, "R$ 420k", "1.4%",
                "Expansão para mercado Latino-Americano", "Most Consistent");
            startupRepo.save(agronova);

            Startup vitalia = startup("Vitalia", "Healthtech",
                "Prontuário eletrônico para clínicas independentes.",
                "Sistema de prontuário eletrônico simplificado para clínicas de pequeno e médio porte.",
                "Vitalia torna o prontuário eletrônico acessível para clínicas independentes, com interface simples e integração com planos de saúde.",
                "MVP", "Belo Horizonte, MG",
                "R$ 28k", 320, "+24%", 18, "R$ 28k", "3.8%",
                "Atingir 50 clínicas pagantes", "Rising");
            startupRepo.save(vitalia);

            Startup lumen = startup("Lumen", "Edtech",
                "Aprendizado adaptativo com IA para o ensino básico.",
                "Plataforma de educação adaptativa que usa IA para personalizar o aprendizado.",
                "Lumen identifica lacunas no aprendizado de cada aluno e cria trilhas personalizadas com exercícios, vídeos e gamificação.",
                "Primeiros usuários", "Florianópolis, SC",
                "R$ 8k", 890, "+45%", 4, "R$ 8k", "5.2%",
                "Fechar parceria com 5 escolas públicas", "Trending");
            startupRepo.save(lumen);

            // --- Memberships ---
            membership(mariana.getId(), finpulse.getId(), "Owner", membershipRepo);
            membership(julia.getId(), vitalia.getId(), "Owner", membershipRepo);
            membership(camila.getId(), lumen.getId(), "Owner", membershipRepo);
            membership(pedro.getId(), agronova.getId(), "Owner", membershipRepo);
            membership(marcos.getId(), finpulse.getId(), "Advisor", membershipRepo);

            // --- Posts ---
            Post p1 = new Post();
            p1.setAuthorId(finpulse.getId());
            p1.setStartupId(finpulse.getId());
            p1.setAuthorName("FinPulse");
            p1.setAuthorType("Startup");
            p1.setAuthorPhoto("F");
            p1.setType("STRUCTURED_UPDATE");
            p1.setBadge("Update");
            p1.getMetrics().add(new Metric("RECEITA MRR", "R$ 136k", "R$ 180k"));
            p1.getMetrics().add(new Metric("USUÁRIOS ATIVOS", "920", "1.200"));
            p1.setContent("Fechamos o melhor mês da nossa história! Crescimento puxado pelo novo módulo de conciliação automática. Agradeço à equipe incrível.");
            p1.setLikesCount(142);
            p1.setCommentsCount(28);
            postRepo.save(p1);

            Post p2 = new Post();
            p2.setAuthorId(julia.getId());
            p2.setAuthorName("Julia Mendes");
            p2.setAuthorType("Founder");
            p2.setAuthorPhoto("https://i.pravatar.cc/150?img=1");
            p2.setType("FREE_TEXT");
            p2.setContent("Bastidores do nosso último sprint: 5 dias de hackathon interno para acelerar o roadmap de IA. Equipe incrível, produto evoluindo rápido. Healthtech nunca foi tão emocionante! 🚀");
            p2.setLikesCount(89);
            p2.setCommentsCount(12);
            postRepo.save(p2);

            Post p3 = new Post();
            p3.setAuthorId(agronova.getId());
            p3.setStartupId(agronova.getId());
            p3.setAuthorName("AgroNova");
            p3.setAuthorType("Startup");
            p3.setAuthorPhoto("A");
            p3.setType("STRUCTURED_UPDATE");
            p3.setBadge("Crescimento");
            p3.getMetrics().add(new Metric("CLIENTES", "98", "156"));
            p3.getMetrics().add(new Metric("MRR", "R$ 310k", "R$ 420k"));
            p3.setContent("Expansão acelerada: fechamos 58 novos clientes só no último trimestre. O mercado agro digital está em plena ebulição.");
            p3.setLikesCount(203);
            p3.setCommentsCount(41);
            postRepo.save(p3);

            Post p4 = new Post();
            p4.setAuthorId(lumen.getId());
            p4.setStartupId(lumen.getId());
            p4.setAuthorName("Lumen");
            p4.setAuthorType("Startup");
            p4.setAuthorPhoto("L");
            p4.setType("FREE_TEXT");
            p4.setContent("Chegamos a 890 alunos usando a plataforma! Estamos vendo crianças de 8 anos completando módulos de matemática avançada de forma divertida. Isso que é impacto real.");
            p4.setLikesCount(67);
            p4.setCommentsCount(9);
            postRepo.save(p4);

            Post p5 = new Post();
            p5.setAuthorId(mariana.getId());
            p5.setAuthorName("Mariana Silva");
            p5.setAuthorType("Founder");
            p5.setAuthorPhoto("https://i.pravatar.cc/150?img=5");
            p5.setType("FREE_TEXT");
            p5.setContent("Acabo de voltar do maior evento de fintech da América Latina. Uma coisa ficou clara: o mercado está faminto por soluções de tesouraria inteligente para médias empresas. A FinPulse está no lugar certo, na hora certa.");
            p5.setLikesCount(54);
            p5.setCommentsCount(7);
            postRepo.save(p5);

            // --- Follows ---
            follow(mariana.getId(), "STARTUP", agronova.getId(), followRepo);
            follow(mariana.getId(), "STARTUP", lumen.getId(), followRepo);
            follow(julia.getId(), "STARTUP", finpulse.getId(), followRepo);
            follow(rafael.getId(), "STARTUP", finpulse.getId(), followRepo);
            follow(rafael.getId(), "STARTUP", agronova.getId(), followRepo);
            follow(ana.getId(), "STARTUP", vitalia.getId(), followRepo);
            follow(marcos.getId(), "STARTUP", finpulse.getId(), followRepo);

            // --- Messages (seed some conversations) ---
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

    private Startup startup(String name, String sector, String shortDesc, String desc, String fullDesc,
                            String stage, String location, String revenue, Integer users, String growth,
                            Integer clients, String mrr, String churn, String objective, String badges) {
        Startup s = new Startup();
        s.setName(name); s.setSector(sector); s.setShortDescription(shortDesc);
        s.setDescription(desc); s.setFullDescription(fullDesc); s.setStage(stage);
        s.setLocation(location); s.setMonthlyRevenue(revenue); s.setUsersCount(users);
        s.setGrowthPercent(growth); s.setClientsCount(clients); s.setMrr(mrr);
        s.setChurn(churn); s.setCurrentObjective(objective); s.setBadges(badges);
        s.setMetricsPublic(true);
        s.setMainMetrics("Receita: " + revenue + " | Usuários: " + users + " | Crescimento: " + growth);
        return s;
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
