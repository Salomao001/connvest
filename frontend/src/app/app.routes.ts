import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { RegisterComponent } from './register/register.component';
import { ProfileUser } from './profile-user/profile-user';
import { ProfileStartup } from './profile-startup/profile-startup';
import { LayoutComponent } from './layout/layout.component';
import { FeedComponent } from './feed/feed.component';
import { DiscoverComponent } from './discover/discover.component';
import { ProposalsComponent } from './proposals/proposals.component';
import { authGuard } from './auth.guard';
import { OnboardingComponent } from './onboarding/onboarding.component';
import { CreateStartupComponent } from './create-startup/create-startup.component';
import { MyStartupsComponent } from './my-startups/my-startups.component';
import { RankingsComponent } from './rankings/rankings.component';
import { CoFounderMatchComponent } from './co-founder-match/co-founder-match.component';
import { InvestorsComponent } from './investors/investors.component';
import { MessagesComponent } from './messages/messages.component';
import { NotificationsComponent } from './notifications/notifications.component';
import { PipelineComponent } from './pipeline/pipeline.component';
import { PremiumComponent } from './premium/premium.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'onboarding', component: OnboardingComponent, canActivate: [authGuard] },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', component: FeedComponent },
      { path: 'profile', component: ProfileUser },
      { path: 'startup/new', component: CreateStartupComponent },
      { path: 'startup', component: MyStartupsComponent },
      { path: 'startup/:id', component: ProfileStartup },
      { path: 'descobrir', component: DiscoverComponent },
      { path: 'rankings', component: RankingsComponent },
      { path: 'co-founder-match', component: CoFounderMatchComponent },
      { path: 'investidores', component: InvestorsComponent },
      { path: 'mensagens', component: MessagesComponent },
      { path: 'propostas', component: ProposalsComponent },
      { path: 'notificacoes', component: NotificationsComponent },
      { path: 'pipeline', component: PipelineComponent },
      { path: 'premium', component: PremiumComponent }
    ]
  }
];
