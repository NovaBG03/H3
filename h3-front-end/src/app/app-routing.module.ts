import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthenticationComponent} from './authentication/authentication.component';
import {TreeComponent} from './tree/tree.component';
import {AuthGuard} from './authentication/auth.guard';
import {TreeListComponent} from './tree/tree-list/tree-list.component';
import {TreeNewComponent} from './tree/tree-new/tree-new.component';
import {TreeViewComponent} from './tree/tree-view/tree-view.component';
import {TreeSettingsComponent} from './tree/tree-settings/tree-settings.component';
import {UserProfileComponent} from './home/user-profile/user-profile.component';
import {WelcomeComponent} from './home/welcome/welcome.component';
import {SearchComponent} from './search/search.component';
import {ViewGraphComponent} from './member/view-graph/view-graph.component';
import {FactsComponent} from './member/facts/facts.component';
import {SettingsComponent} from './settings/settings.component';
import {UserProfileSettingsComponent} from './settings/user-profile-settings/user-profile-settings.component';
import {UserPreferencesSettingsComponent} from './settings/user-preferences-settings/user-preferences-settings.component';
import {InfoComponent} from './settings/info/info.component';

const routes: Routes = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'home', component: WelcomeComponent, pathMatch: 'full'},
  {path: 'auth', component: AuthenticationComponent},
  {
    path: 'trees', component: TreeComponent, canActivate: [AuthGuard], children: [
      {
        path: 'my', component: TreeListComponent, children: [
          {path: 'new', component: TreeNewComponent},
        ]
      },
      {
        path: 'search', component: SearchComponent, canActivate: [AuthGuard], children: [
          {path: '', component: TreeListComponent, pathMatch: 'full'},
          {path: ':treePattern', component: TreeListComponent}
        ]
      },
      {
        path: ':id', component: TreeViewComponent, canActivate: [AuthGuard], children: [
          {path: 'settings', component: TreeSettingsComponent},
          {path: 'graph', component: ViewGraphComponent},
          {path: 'facts', component: FactsComponent}
        ]
      }
    ]
  },
  {path: 'settings', component: SettingsComponent, canActivate: [AuthGuard], children: [
      {path: 'user', component: UserProfileSettingsComponent},
      {path: 'preferences', component: UserPreferencesSettingsComponent},
      {path: 'trees', component: TreeSettingsComponent},
      {path: 'info', component: InfoComponent}
    ]},
  {path: ':username', component: UserProfileComponent, canActivate: [AuthGuard]}
  // { path: '**', redirectTo: 'home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {relativeLinkResolution: 'legacy'})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
