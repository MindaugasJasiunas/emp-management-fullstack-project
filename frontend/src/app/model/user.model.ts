import { Role } from './role.model';

export class User {
  constructor(
    public publicId: string,
    public username: string,
    public email: string,
    public firstName: string,
    public lastName: string,
    public profileImageUrl: string,
    public dateOfBirth: string,
    public age: number,
    public joinDate: string,
    public lastLoginDate: string,
    public active: boolean,
    public notLocked: boolean,
    public roles: Role[]
  ) {}
}
