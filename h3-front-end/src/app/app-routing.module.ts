import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthenticationComponent} from './authentication/authentication.component';
import {TreeComponent} from './tree/tree.component';
import {AuthGuard} from './authentication/auth.guard';
import {TreeListComponent} from './tree/tree-list/tree-list.component';
import {TreeNewComponent} from './tree/tree-new/tree-new.component';
import {TreeViewComponent} from './tree/tree-view/tree-view.component';
import {ViewTreeComponent} from './member/view-tree/view-tree.component';
import {ViewTableComponent} from './member/view-table/view-table.component';
import {TreeSettingsComponent} from './tree/tree-settings/tree-settings.component';
import {UserProfileComponent} from './home/user-profile/user-profile.component';
import {WelcomeComponent} from './home/welcome/welcome.component';
import {SearchComponent} from './search/search.component';
import {EmptyComponent} from './shared/empty/empty.component';

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
        path: 'search', component: SearchComponent, children: [
          {path: '', component: TreeListComponent, pathMatch: 'full' },
          {path: ':treePattern', component: TreeListComponent}
        ]
      },
      {
        path: ':id', component: TreeViewComponent, children: [
          {path: 'members', component: ViewTreeComponent},
          {path: 'table', component: ViewTableComponent},
          {path: 'settings', component: TreeSettingsComponent},
        ]
      }
    ]
  },
  {path: ':username', component: UserProfileComponent}
  // { path: '**', redirectTo: 'home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
