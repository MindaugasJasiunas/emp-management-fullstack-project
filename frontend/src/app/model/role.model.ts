import { Authority } from "./authority.model";

export class Role {
  constructor(public id: number, public roleName: string, public authorities?: Authority[]){}
}
