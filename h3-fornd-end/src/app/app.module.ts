import {BrowserModule} from '@angular/platform-browser';
import {ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';

import {NgbCollapseModule, NgbDatepickerModule, NgbTypeaheadModule} from '@ng-bootstrap/ng-bootstrap';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from './home/home.component';
import {HeaderComponent} from './header/header.component';
import {AuthenticationComponent} from './authentication/authentication.component';
import {TreeListComponent} from './tree/tree-list/tree-list.component';
import {TreeItemComponent} from './tree/tree-list/tree-item/tree-item.component';
import {TreeComponent} from './tree/tree.component';
import {AuthInterceptorService} from './authentication/auth-interceptor.service';
import {TreeNewComponent} from './tree/tree-new/tree-new.component';
import {TreeViewComponent} from './tree/tree-view/tree-view.component';
import {ViewTreeComponent} from './member/view-tree/view-tree.component';
import {ViewTableComponent} from './member/view-table/view-table.component';
import {TreeSettingsComponent} from './tree/tree-settings/tree-settings.component';
import { MemberItemComponent } from './member/member-item/member-item.component';
import { MemberSearchComponent } from './member/member-item/member-search/member-search.component';

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
    ViewTreeComponent,
    ViewTableComponent,
    TreeSettingsComponent,
    MemberItemComponent,
    MemberSearchComponent
  ],
    imports: [
        BrowserModule,
        ReactiveFormsModule,
        HttpClientModule,
        NgbCollapseModule,
        AppRoutingModule,
        NgbDatepickerModule,
        NgbTypeaheadModule
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
