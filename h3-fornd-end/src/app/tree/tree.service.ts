import {FamilyTree, FamilyTreeData, FamilyTreeListDTO, FamilyTreeResponseDTO} from '../shared/dtos.model';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {map, take, tap} from 'rxjs/operators';
import {AuthService} from '../authentication/auth.service';

@Injectable({providedIn: 'root'})
export class TreeService {
  createdNewTree = new Subject<FamilyTree>();

  constructor(private http: HttpClient, private authService: AuthService) {
  }

  getOwnTrees(): Observable<FamilyTree[]> {
    let username: string;
    this.authService.user.pipe(take(1)).subscribe(user => username = user.username);
    return this.http.get<FamilyTreeListDTO>('http://localhost:8080/trees/' + username)
      .pipe(map(familyTreeListDTO => {
        return familyTreeListDTO.familyTrees.map(familyTreeResponseDTO => {
          return this.mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO);
        });
      }));
  }

  createTree(familyTreeData: FamilyTreeData): Observable<FamilyTree> {
    return this.http.post<FamilyTreeResponseDTO>('http://localhost:8080/trees/', familyTreeData)
      .pipe(map(familyTreeResponseDTO => {
        return this.mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO);
      }), tap(familyTree => {
        this.createdNewTree.next(familyTree);
      }));
  }

  private mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO: FamilyTreeResponseDTO): FamilyTree {
    return new FamilyTree(familyTreeResponseDTO.id,
      familyTreeResponseDTO.name,
      familyTreeResponseDTO.isPrivate,
      new Date(familyTreeResponseDTO.createdAt));
  }
}
