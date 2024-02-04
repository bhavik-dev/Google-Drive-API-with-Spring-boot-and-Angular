import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from "rxjs/operators";
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  api_token : string | null = '';
  url = environment.api_url;

  constructor( private http : HttpClient )
  {
    this.loadToken();
  }

  login()
  {
    const url = `${this.url}/login`;
    return this.http.get(url, {observe: 'response'});
  }

  logout()
  {
    const url = `${this.url}/logout/${this.token}`;

    return this.http.get(url).pipe(
      map( (resp : any) => {
        this.removeToken();
        return resp;
      })
    );
  }

  saveToken( token : string )
  {
    this.api_token = token;
    localStorage.setItem('token', token);

    let hoy = new Date();
    hoy.setSeconds( 3600 );

    localStorage.setItem( 'expira', hoy.getTime().toString() );
  }

  removeToken()
  {
    localStorage.removeItem('token');
    localStorage.removeItem('expira');
  }

  loadToken()
  {
    if ( localStorage.getItem('token') == null ) {
      this.api_token = '';
    } else {
      this.api_token = localStorage.getItem('token');
    }
    return this.api_token;
  }

  get token()
  {
    return this.api_token;
  }

  isAuth()
  {
    if ( this.api_token != null && this.api_token.length < 2  ) {
      return false;
    }
    const expira = Number(localStorage.getItem('expira'));
    const expiraDate = new Date();
    expiraDate.setTime(expira);

    if ( expiraDate > new Date() ) {
      return true;
    } else {
      return false;
    }
  }
}
