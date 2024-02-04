import { AuthService } from 'src/app/services/auth.service';
import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { File } from 'src/app/models/file';
import { FileService } from 'src/app/services/file.service';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTreeNestedDataSource} from '@angular/material/tree';
import { NestedTreeControl} from '@angular/cdk/tree';
import { Permission } from 'src/app/models/permission';
import { ValidatorsService } from 'src/app/services/validators.service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {

  allFiles    : File[]       = [];
  files       : File[]       = [];
  form        : FormGroup    = new FormGroup({});
  loading     : boolean      = false;
  permissions : Permission[] = [];
  dataSource  = new MatTreeNestedDataSource<File>();
  treeControl = new NestedTreeControl<File>(node => node.children);

  hasChild = (_: number, node: File) => !!node.children && node.children.length > 0;
  visible  = (_: number, node: File) => node.visible;

  constructor(
    private fileService : FileService ,
    private fb : FormBuilder ,
    private validators : ValidatorsService,
    private _snackBar: MatSnackBar
  ) {
    this.dataSource.data = [];
    this.form = this.fb.group({
      email : ['' , [Validators.required , Validators.pattern('[a-z0-9._%+-]{6,30}@gmail.com') , this.validators.ownerEmail ]],
      permission : [''],
      files : this.fb.array([])
    });
  }

  ngOnInit(): void
  {
    this.initFiles();
  }

  initFiles()
  {
    this.loading = true;
    this.form.reset();
    this.form.get('email')?.markAsPristine();
    this.fileService.files().subscribe(( resp : File[] )=>{
      this.files    = [];
      this.allFiles = [];
      this.files = resp;
      this.dataSource.data = this.files;
      this.allFiles = this.files;
      this.loading = false;
      this.onEmailChange();
      this.initPermission();
    }, error => {
      console.error(error);
      this.loading = false;
      this.openSnackBar('There was an error on the web app' , 'Close');
    });
  }

  initPermission()
  {
    this.findPermission(this.files);
  }

  private findPermission( files : File[] )
  {
    files.forEach( file => {
      file.permissions.forEach(permission => {
        if (!this.permissions.some(perm => perm.id == permission.id)) {
          this.permissions.push(permission);
        }
      });
      if (file.hasChild()) {
        this.findPermission(file.children);
      }
    })
  }

  get invalidEmail()
  {
    return this.form.get('email')?.invalid;
  }

  get disabledSubmit()
  {
    return this.invalidEmail || this.files.length === 0;
  }

  owner( permission : Permission )
  {
    return permission.emailAddress == environment.owner_email;
  }

  get emailError()
  {
    if (this.form.get('email')?.hasError('owner_email')) {
      return 'That is the owner email.';
    }else {
      return 'Enter a valid Gmail address.';
    }
  }

  openSnackBar( message: string, action: string )
  {
    this._snackBar.open(message, action, {
      duration: 4000,
    });
  }

  isFolder( node : any ) : boolean
  {
    return node.mimeType == 'application/vnd.google-apps.folder';
  }

  unshare()
  {
    if (this.form.invalid) {
      this.form.get('email')?.markAsTouched();
      return;
    }
    this.fileService.unshare(this.form.value).subscribe( () => {
      this.openSnackBar('Successfully revoked permissions' , 'Close');
      this.initFiles();
    }, error =>{
      console.error(error);
      this.openSnackBar('There was an error on the web app' , 'Close');
    });
  }

  onEmailChange()
  {
    this.form.get('email')?.valueChanges.subscribe( email => {
      this.filter(email);
      this.dataSource.data = [];
      this.dataSource.data = this.files;
      this.populateFilesArr(this.files , email,  true);
      if(!this.disabledSubmit) {
        let permission = this.files[0].permissionId(email);
        this.form.get('permission')?.setValue(permission);
      }
    });
  }

  setEmail( permission : Permission )
  {
    this.form.get('email')?.setValue(permission.emailAddress);
    this.form.get('email')?.updateValueAndValidity();
  }

  filter( email : string )
  {
    if (email == '') {
      this.unHide();
    } else {
      this.filterInner(this.files , email);
    }
  }

  unHide()
  {
    this.files.forEach(item=>item.hide(false));
  }

  filterInner( files : File[] , email : string )
  {
    files.forEach(item => {
      if (item.hasPermission(email)) {
        item.hide(false);
      }else {
        item.hide(true);
      }
      if (item.hasChild()) {
        this.filterInner(item.children , email);
      }
    });
  }

  populateFilesArr( files : File[] , email : string, root : boolean )
  {
    let arr = this.form.controls['files'] as FormArray;

    if (root) {arr.clear();}

    for ( const file of files ) {
      if(file.hasChild()){
        this.populateFilesArr(file.children , email, false);
      }
      if (file.hasPermissionOnlyRoot(email)) {
        arr.push(new FormControl(file.id));
      }
    }
  }
}
