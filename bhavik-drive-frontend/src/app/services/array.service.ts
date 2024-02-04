import { Injectable } from '@angular/core';
import { File } from '../models/file';
import { Permission } from '../models/permission';

@Injectable({
  providedIn: 'root'
})
export class ArrayService {

  constructor() { }

  public createArray( filesObj : object )
  {
    let files : File[];
    if (filesObj == null) { return []; }
    files = this.fileArr(filesObj);
    return files;
  }

  private fileArr( filesObj : object )
  {
    let files : File[] = [];
    Object.values(filesObj).forEach( ( body:any ) => {
      let file : File = new File(body.id , body.name , body.mimeType);
      file.permissions = this.permArr(body.permissions.permissions);
      if (body.children.length != 0) {
        file.children = this.fileArr(body.children);
      }
      files.push(file);
    });
    return files;
  }

  permArr( permObj : object ) {
    let permissions : Permission[] = [];
    Object.values(permObj).forEach((body:any) => {
      let permission : Permission = new Permission(body.id , body.emailAddress );
      permissions.push(permission);
    });
    return permissions;
  }
}
