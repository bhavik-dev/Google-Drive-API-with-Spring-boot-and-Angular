import { ArrayService } from './array.service';
import { AuthService } from './auth.service';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  url :string = environment.api_url;

  constructor( private http : HttpClient , private authService : AuthService , private arrayService : ArrayService ) {}

  files()
  {
    const url = `${this.url}/api/list`;

    let data : any = {api_token : null};

    if(this.authService.token){
      data.api_token = this.authService.token;
    }

    return this.http.post(url , data , {observe : 'response'}).pipe(map( (resp : any) =>{
      return this.arrayService.createArray(resp.body.files);
    }));
  }

  unshare( data : any )
  {
    const url = `${this.url}/api/unshare`;

    if(this.authService.token){
      data.api_token = this.authService.token;
    }

    return this.http.post(url , data , {observe : 'response'});
  }
}
