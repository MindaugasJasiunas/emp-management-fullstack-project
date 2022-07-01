import { Role } from './role.model';

export class User {
  constructor(
    public publicId: string | null,
    public username: string,
    public email: string,
    public password: string | null,
    public firstName: string,
    public lastName: string,
    public profileImageUrl: string,
    public dateOfBirth: string,
    public active: boolean,
    public notLocked: boolean,
    public roles: Role[] | null,
    public joinDate?: string,
    public lastLoginDate?: string,
    public age?: number | null,
  ) {}
}
