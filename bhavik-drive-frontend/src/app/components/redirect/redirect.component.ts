import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-redirect',
  template: ``,
  styles: [
  ]
})
export class RedirectComponent implements OnInit {

  constructor( private route : ActivatedRoute , private authService : AuthService , private router : Router) { }

  ngOnInit(): void
  {
    let token = this.route.snapshot.paramMap.get('token');
    if(token){
      this.authService.saveToken(token);
    }
    this.router.navigateByUrl('/google/drive/files');
  }
}
