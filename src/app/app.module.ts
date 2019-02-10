import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {MatSliderModule} from '@angular/material/slider';

import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AuthInterceptor} from './interceptors/authInterceptor';
import { UserSettingsComponent } from './components/user-settings/user-settings.component';
import { HomeComponent } from './components/home/home.component';
import { AppRoutingModule } from './app-routing.module';
import {RouterModule} from '@angular/router';
import { RegisterComponent } from './components/register/register.component';
import { GameRoomComponent } from './components/game-room/game-room.component';
import { PlayerComponent } from './components/player/player.component';
import { GameTableComponent } from './components/game-table/game-table.component';
import { CardComponent } from './components/card/card.component';
import { ActionbarComponent } from './components/actionbar/actionbar.component';
import { ChatComponent } from './components/chat/chat.component';
import { RoomsOverviewComponent } from './components/rooms-overview/rooms-overview.component';
import {
  AuthServiceConfig,
  FacebookLoginProvider,
  SocialLoginModule
} from 'angularx-social-login';

const config = new AuthServiceConfig([
  {
    id: FacebookLoginProvider.PROVIDER_ID,
    provider: new FacebookLoginProvider('483584635507231')
  }
]);

export function provideConfig() {
  return config;
}

import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    UserSettingsComponent,
    HomeComponent,
    RegisterComponent,
    GameRoomComponent,
    PlayerComponent,
    GameTableComponent,
    CardComponent,
    ActionbarComponent,
    ChatComponent,
    RoomsOverviewComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatSliderModule,
    FormsModule,
    RouterModule,
    ReactiveFormsModule,
    HttpClientModule,
    AppRoutingModule,
    SocialLoginModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      provide: AuthServiceConfig,
      useFactory: provideConfig
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
