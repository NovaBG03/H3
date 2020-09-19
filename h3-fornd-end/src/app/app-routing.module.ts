import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {AuthenticationComponent} from './authentication/authentication.component';
import {TreeComponent} from './tree/tree.component';
import {AuthGuard} from './authentication/auth.guard';
import {TreeListComponent} from './tree/tree-list/tree-list.component';
import {TreeNewComponent} from './tree/tree-new/tree-new.component';

const routes: Routes = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'home', component: HomeComponent, pathMatch: 'full'},
  {path: 'auth', component: AuthenticationComponent},
  {
    path: 'trees', component: TreeComponent, canActivate: [AuthGuard], children: [
      {
        path: 'my', component: TreeListComponent, children: [
          {path: 'new', component: TreeNewComponent}
        ]
      },
    ]
  },
  // { path: '**', redirectTo: 'home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
