import {BrowserModule} from '@angular/platform-browser';
import {ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';

import {NgbCollapseModule, NgbDatepickerModule, NgbTypeaheadModule} from '@ng-bootstrap/ng-bootstrap';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './header/header.component';
import {AuthenticationComponent} from './authentication/authentication.component';
import {TreeListComponent} from './tree/tree-list/tree-list.component';
import {TreeComponent} from './tree/tree.component';
import {AuthInterceptorService} from './authentication/auth-interceptor.service';
import {TreeNewComponent} from './tree/tree-new/tree-new.component';
import {TreeViewComponent} from './tree/tree-view/tree-view.component';
import {ViewTreeComponent} from './member/view-tree/view-tree.component';
import {ViewTableComponent} from './member/view-table/view-table.component';
import {TreeSettingsComponent} from './tree/tree-settings/tree-settings.component';
import {MemberItemComponent} from './member/member-item/member-item.component';
import {MemberSearchComponent} from './member/member-item/member-search/member-search.component';
import {AlertComponent} from './shared/alert/alert.component';
import {WelcomeComponent} from './home/welcome/welcome.component';
import {UserProfileComponent} from './home/user-profile/user-profile.component';
import {ImageCropperModule} from 'ngx-image-cropper';
import {Ng2ImgMaxModule} from 'ng2-img-max';
import { ImageUploaderComponent } from './shared/image-uploader/image-uploader.component';
import { SearchComponent } from './search/search.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    AuthenticationComponent,
    TreeListComponent,
    TreeComponent,
    TreeNewComponent,
    TreeViewComponent,
    ViewTreeComponent,
    ViewTableComponent,
    TreeSettingsComponent,
    MemberItemComponent,
    MemberSearchComponent,
    AlertComponent,
    WelcomeComponent,
    UserProfileComponent,
    ImageUploaderComponent,
    SearchComponent
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbCollapseModule,
    AppRoutingModule,
    NgbDatepickerModule,
    NgbTypeaheadModule,
    ImageCropperModule,
    Ng2ImgMaxModule
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
