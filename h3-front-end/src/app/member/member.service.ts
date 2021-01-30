import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {
  Couple,
  CoupleListDTO,
  CoupleResponseDTO,
  FamilyMember,
  FamilyMemberDataDTO, FamilyMemberListDTO,
  FamilyMemberResponseDTO,
  Gender,
  MessageDTO
} from '../shared/dtos.model';
import {environment} from '../../environments/environment';

@Injectable({providedIn: 'root'})
export class MemberService {
  constructor(private http: HttpClient) {
  }

  getCouples(treeId: number): Observable<Couple[]> {
    return this.http.get<CoupleListDTO>(environment.domain + '/trees/' + treeId + '/members/couples')
      .pipe(map(coupleListDTO => {
        const couples = coupleListDTO.couples.map(coupleResponseDTO => {
          return this.mapCoupleResponseDTOToCouple(coupleResponseDTO);
        });
        return couples;
      }));
  }

  getFamilyMembers(treeId: number): Observable<FamilyMember[]> {
    return this.http.get<FamilyMemberListDTO>(environment.domain + '/trees/' + treeId + '/members')
      .pipe(map(familyMemberListDTO => {
        const members = familyMemberListDTO.familyMembers.map(familyMemberResponseDTO => {
          return this.mapFamilyMemberResponseDTOToFamilyMember(familyMemberResponseDTO);
        });
        return members;
      }));
  }

  updateMember(treeId: number, memberId: number, member: FamilyMemberDataDTO): Observable<FamilyMember> {
    return this.http.put<FamilyMemberResponseDTO>(`${environment.domain}/trees/${treeId}/members/${memberId}`, member)
      .pipe(map(familyMemberResponseDTO => {
        return this.mapFamilyMemberResponseDTOToFamilyMember(familyMemberResponseDTO);
      }));
  }

  createMember(treeId: number, member: FamilyMemberDataDTO): Observable<FamilyMember> {
    return this.http.post<FamilyMemberResponseDTO>(`${environment.domain}/trees/${treeId}/members/`, member)
      .pipe(map(familyMemberResponseDTO => {
        return this.mapFamilyMemberResponseDTOToFamilyMember(familyMemberResponseDTO);
      }));
  }

  deleteMember(treeId: number, memberId: number): Observable<string> {
    return this.http.delete<MessageDTO>(`${environment.domain}/trees/${treeId}/members/${memberId}`)
      .pipe(map(messageDto => messageDto.message));
  }

  private mapFamilyMemberResponseDTOToFamilyMember(familyMemberResponseDTO: FamilyMemberResponseDTO): FamilyMember {
    const birthday: Date = familyMemberResponseDTO.birthday ? new Date(familyMemberResponseDTO.birthday) : null;
    const dateOfDeath: Date = familyMemberResponseDTO.dateOfDeath ? new Date(familyMemberResponseDTO.dateOfDeath) : null;

    return new FamilyMember(familyMemberResponseDTO.id,
      familyMemberResponseDTO.firstName,
      familyMemberResponseDTO.lastName,
      birthday,
      dateOfDeath,
      Gender[familyMemberResponseDTO.gender]);
  }

  private mapCoupleResponseDTOToCouple(coupleResponseDTO: CoupleResponseDTO): Couple {
    return {
      primaryParentId: coupleResponseDTO.primaryParentId,
      partnerParentId: coupleResponseDTO.partnerParentId,
      primaryParentName: coupleResponseDTO.primaryParentName,
      partnerParentName: coupleResponseDTO.partnerParentName,
      treeId: coupleResponseDTO.treeId,
      leftIndex: coupleResponseDTO.leftIndex,
      rightIndex: coupleResponseDTO.rightIndex,
      depthIndex: coupleResponseDTO.depthIndex
    };
  }
}
