import {BrowserModule} from '@angular/platform-browser';
import {ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';

import {NgbCollapseModule, NgbDatepickerModule, NgbTypeaheadModule, NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './header/header.component';
import {AuthenticationComponent} from './authentication/authentication.component';
import {TreeListComponent} from './tree/tree-list/tree-list.component';
import {TreeComponent} from './tree/tree.component';
import {AuthInterceptorService} from './authentication/auth-interceptor.service';
import {TreeNewComponent} from './tree/tree-new/tree-new.component';
import {TreeViewComponent} from './tree/tree-view/tree-view.component';
import {TreeSettingsComponent} from './tree/tree-settings/tree-settings.component';
import {MemberItemComponent} from './member/member-item/member-item.component';
import {MemberSearchComponent} from './member/member-item/member-search/member-search.component';
import {AlertComponent} from './shared/alert/alert.component';
import {WelcomeComponent} from './home/welcome/welcome.component';
import {UserProfileComponent} from './home/user-profile/user-profile.component';
import {ImageCropperModule} from 'ngx-image-cropper';
import {Ng2ImgMaxModule} from 'ng2-img-max';
import {ImageUploaderComponent} from './shared/image-uploader/image-uploader.component';
import {SearchComponent} from './search/search.component';
import {EmptyComponent} from './shared/empty/empty.component';
import {LoadingComponent} from './shared/loading/loading.component';
import {ViewGraphComponent} from './member/view-graph/view-graph.component';
import {MemberContextmenuComponent} from './member/member-contextmenu/member-contextmenu.component';
import {FactsComponent} from './member/facts/facts.component';
import {SettingsComponent} from './settings/settings.component';
import {UserProfileSettingsComponent} from './settings/user-profile-settings/user-profile-settings.component';
import {UserPreferencesSettingsComponent} from './settings/user-preferences-settings/user-preferences-settings.component';
import {InfoComponent} from './settings/info/info.component';
import { AllTreesSettingsComponent } from './settings/all-trees-settings/all-trees-settings.component';
import { FactCardComponent } from './member/facts/fact-card/fact-card.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    AuthenticationComponent,
    TreeListComponent,
    TreeComponent,
    TreeNewComponent,
    TreeViewComponent,
    TreeSettingsComponent,
    MemberItemComponent,
    MemberSearchComponent,
    AlertComponent,
    WelcomeComponent,
    UserProfileComponent,
    ImageUploaderComponent,
    SearchComponent,
    EmptyComponent,
    LoadingComponent,
    ViewGraphComponent,
    MemberContextmenuComponent,
    FactsComponent,
    SettingsComponent,
    UserProfileSettingsComponent,
    UserPreferencesSettingsComponent,
    InfoComponent,
    AllTreesSettingsComponent,
    FactCardComponent
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
    Ng2ImgMaxModule,
    NgbModule
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
