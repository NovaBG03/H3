import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {FamilyMember, FamilyMemberListDTO, FamilyMemberResponseDTO, FamilyMembers} from '../shared/dtos.model';

@Injectable({providedIn: 'root'})
export class MemberService {
  constructor(private http: HttpClient) {
  }

  getMembers(treeId: number): Observable<FamilyMembers> {
    return this.http.get<FamilyMemberListDTO>('http://localhost:8080/trees/' + treeId + '/members')
      .pipe(map(familyMemberListDTO => {
        const members = familyMemberListDTO.familyMembers.map(familyMemberDTO => {
          return this.mapFamilyMemberResponseDTOToFamilyMember(familyMemberDTO);
        });

        return new FamilyMembers(members);
      }));
  }

  private mapFamilyMemberResponseDTOToFamilyMember(familyMemberResponseDTO: FamilyMemberResponseDTO): FamilyMember {
    const partners: number[] = [];
    familyMemberResponseDTO.partners.forEach(partner => partners.push(+partner));

    const birthday: Date = familyMemberResponseDTO.birthday ? new Date(familyMemberResponseDTO.birthday) : null;
    const dateOfDeath: Date = familyMemberResponseDTO.dateOfDeath ? new Date(familyMemberResponseDTO.dateOfDeath) : null;

    return new FamilyMember(familyMemberResponseDTO.id,
      familyMemberResponseDTO.firstName,
      familyMemberResponseDTO.lastName,
      birthday,
      dateOfDeath,
      familyMemberResponseDTO.gender,
      +familyMemberResponseDTO.primaryParentId,
      +familyMemberResponseDTO.secondaryParentId,
      partners);
  }
}
