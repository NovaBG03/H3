import {FamilyTree, FamilyTreeListDTO, FamilyTreeResponseDTO} from '../shared/dtos.model';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map, take} from 'rxjs/operators';
import {AuthService} from '../authentication/auth.service';

@Injectable({providedIn: 'root'})
export class TreeService {
  private trees: FamilyTree[] = [
    new FamilyTree(1, 'Test Tree', true, new Date()),
    new FamilyTree(2, 'Custom Tree', false, new Date()),
    new FamilyTree(3, 'Another Tree', true, new Date()),
    new FamilyTree(3, 'Another Tree', true, new Date()),
  ];

  constructor(private http: HttpClient, private authService: AuthService) {
  }

  getOwnTrees(): Observable<FamilyTree[]> {
    let username: string;
    this.authService.user.pipe(take(1)).subscribe(user => username = user.username);
    return this.http.get<FamilyTreeListDTO>('http://localhost:8080/trees/' + username)
      .pipe(map(familyTreeListDTO => {
        console.log(familyTreeListDTO);
        return familyTreeListDTO.familyTrees.map(familyTreeResponseDTO => {
          return this.mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO);
        });
      }));
  }

  test(): Observable<any> {
    return this.http.get('http://localhost:8080/test');
  }

  private mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO: FamilyTreeResponseDTO): FamilyTree {
    return new FamilyTree(familyTreeResponseDTO.id,
      familyTreeResponseDTO.name,
      familyTreeResponseDTO.isPrivate,
      new Date(familyTreeResponseDTO.createdAt));
  }
}
