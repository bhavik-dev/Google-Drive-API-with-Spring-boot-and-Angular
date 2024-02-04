
export class Permission {

  id            : string;
  emailAddress? : string;

  constructor(
    id : string ,
    emailAddress? :string
  ) {
    this.id = id;
    this.emailAddress = emailAddress;
  }

  includes( substring : string )
  {
    return this.emailAddress?.toLowerCase().includes(substring);
  }

}

