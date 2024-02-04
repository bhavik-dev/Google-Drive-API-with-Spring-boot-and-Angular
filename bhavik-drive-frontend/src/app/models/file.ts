import { Permission } from "./permission";

export class File {

  id             : string;
  name           : string;
  mimeType       : string;
  permissions    : Permission[];
  children       : File[];
  private hidden : boolean = false;

  constructor(
    id : string,
    name :string,
    mimeType : string
  ) {
    this.id = id;
    this.name = name;
    this.mimeType = mimeType;
    this.permissions = [];
    this.children = [];
  }

  get visible()
  {
    return !this.hidden;
  }

  permissionId( email : string ) : string
  {
    let permission = this.permissions.find(perm => perm.emailAddress == email);
    if (permission) {
      return permission.id;
    }else if (this.hasChild()) {
      let id = '';
      for (const child of this.children) {
        if (child.hasPermission(email)) {
          id = child.permissionId(email);
        }
      }
      return id;
    }else {
      return '';
    }
  }

  hasPermissionOnlyRoot( email : string )
  {
    let hasPermission = this.permissions.some((permission) => permission.includes(email));
    return hasPermission;
  }

  hasPermission( email : string )
  {
    let hasPermission = this.hasPermissionOnlyRoot(email);
    if (this.children.length != 0) {
      hasPermission = this.children.some(file => file.hasPermission(email));
    }
    return hasPermission;
  }

  hasChild()
  {
    return this.children.length != 0;
  }

  hide( hide : boolean )
  {
    this.hidden = hide;
    if (this.hasChild()) {
      for (const file of this.children) {
        file.hide(hide);
      }
    }
  }

}
