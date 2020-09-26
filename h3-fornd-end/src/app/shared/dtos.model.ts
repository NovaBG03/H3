export class UserData {
  constructor(public username: string, public email: string, public password: string) {
  }
}

export class UserToken {
  constructor(public token: string,
              public expiresIn: Date,
              public id: number,
              public username: string,
              public email: string,
              public roles: string[]) {
  }

  get isExpired(): boolean {
    return this.expiresIn.getTime() < new Date().getTime();
  }

  get timeTillExpiration(): number {
    return this.expiresIn.getTime() - new Date().getTime();
  }
}

export class UserTokenDTO {
  constructor(public token: string,
              public expiresIn: string,
              public id: string,
              public username: string,
              public email: string,
              public roles: string[]) {
  }
}


export class FamilyTree {
  constructor(public id: number,
              public name: string,
              public isPrivate: boolean,
              public createdAt: Date) {
  }
}

export class FamilyTreeResponseDTO {
  constructor(public id: number,
              public name: string,
              public isPrivate: boolean,
              public createdAt: string) {
  }
}

export class FamilyTreeListDTO {
  constructor(public familyTrees: FamilyTreeResponseDTO[]) {
  }
}

export class FamilyTreeData {
  constructor(public name: string, public isPrivate: boolean) {
  }
}


export class FamilyMembers {
  constructor(members: FamilyMember[]) {
    this.members = members;
  }

  // tslint:disable-next-line:variable-name
  private _mainMember: FamilyMember = null;

  public get mainMember(): FamilyMember {
    return this._mainMember;
  }

  // tslint:disable-next-line:variable-name
  private _members: FamilyMember[] = [];

  public get members(): FamilyMember[] {
    return this._members.slice();
  }

  public set members(members: FamilyMember[]) {
    if (members) {
      this._members = members.slice();
      this._mainMember = members.sort((a, b) => a.id - b.id)[0];
    } else {
      this._members = null;
      this._mainMember = null;
    }
  }

  public getMember(id: number): FamilyMember {
    return this._members.find(member => member.id === id);
  }

  public getChildren(id: number): FamilyMember[] {
    const children: FamilyMember[] = [];

    children.push(...this._members.filter(member => member.primaryParentId === id));
    children.push(...this._members.filter(member => member.secondaryParentId === id));

    return children;
  }
}

export class FamilyMember {
  constructor(public id: number,
              public firstName: string,
              public lastName: string,
              public birthday: Date,
              public dateOfDeath: Date,
              public gender: string,
              public primaryParentId: number,
              public secondaryParentId: number,
              public partners: number[]) {
  }

  get fullName(): string {
    return this.firstName + ' ' + this.lastName;
  }

  get isDirectHeir(): boolean {
    return !!this.primaryParentId;
  }
}

export class FamilyMemberResponseDTO {
  constructor(public id: number,
              public firstName: string,
              public lastName: string,
              public birthday: string,
              public dateOfDeath: string,
              public gender: string,
              public primaryParentId: number,
              public secondaryParentId: number,
              public partners: number[]) {
  }
}

export class FamilyMemberListDTO {
  constructor(public familyMembers: FamilyMemberResponseDTO[]) {
  }
}
