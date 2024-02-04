import { Injectable } from '@angular/core';
import { FormControl } from '@angular/forms';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ValidatorsService {

  constructor() { }

  ownerEmail( control: FormControl )
  {
    if ( control.value?.toLowerCase() === environment.owner_email ){
      return {
        owner_email: true
      }
    }
    return null;
  }
}
