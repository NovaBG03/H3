import {BrowserModule} from '@angular/platform-browser';
import {ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';

import {NgbCollapseModule} from '@ng-bootstrap/ng-bootstrap';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from './home/home.component';
import {HeaderComponent} from './header/header.component';
import {AuthenticationComponent} from './authentication/authentication.component';
import {TreeListComponent} from './tree/tree-list/tree-list.component';
import {TreeItemComponent} from './tree/tree-list/tree-item/tree-item.component';
import {TreeComponent} from './tree/tree.component';
import {AuthInterceptorService} from './authentication/auth-interceptor.service';
import { TreeNewComponent } from './tree/tree-new/tree-new.component';
import { TreeViewComponent } from './tree/tree-view/tree-view.component';
import { MembersComponent } from './members/members.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    HeaderComponent,
    AuthenticationComponent,
    TreeListComponent,
    TreeItemComponent,
    TreeComponent,
    TreeNewComponent,
    TreeViewComponent,
    MembersComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbCollapseModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptorService,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
