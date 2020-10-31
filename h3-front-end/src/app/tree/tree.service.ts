import {FamilyTree, FamilyTreeDataDTO, FamilyTreeListDTO, FamilyTreeResponseDTO} from '../shared/dtos.model';
import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {map, switchMap, take, tap} from 'rxjs/operators';
import {AuthService} from '../authentication/auth.service';

@Injectable({providedIn: 'root'})
export class TreeService {
  createdNewTree = new Subject<FamilyTree>();

  constructor(private http: HttpClient, private authService: AuthService) {
  }

  getOwnTrees(): Observable<FamilyTree[]> {
    return this.authService.user.pipe(
      take(1),
      switchMap(user => {
        return this.http.get<FamilyTreeListDTO>('http://localhost:8080/trees/' + user.username)
          .pipe(map(familyTreeListDTO => {
            return familyTreeListDTO.familyTrees.map(familyTreeResponseDTO => {
              return this.mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO);
            });
          }));
      }));
  }

  createTree(familyTreeData: FamilyTreeDataDTO): Observable<FamilyTree> {
    return this.http.post<FamilyTreeResponseDTO>('http://localhost:8080/trees/', familyTreeData)
      .pipe(map(familyTreeResponseDTO => {
        return this.mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO);
      }), tap(familyTree => {
        this.createdNewTree.next(familyTree);
      }));
  }

  findTree(treePattern: string): Observable<FamilyTree[]> {
    return this.http.get<FamilyTreeListDTO>('http://localhost:8080/trees/', {
      params: new HttpParams().append('treePattern', treePattern)
    })
      .pipe(map(familyTreeListDTO => {
        return familyTreeListDTO.familyTrees.map(familyTreeResponseDTO => {
          return this.mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO);
        });
      }));
  }

  private mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO: FamilyTreeResponseDTO): FamilyTree {
    return new FamilyTree(familyTreeResponseDTO.id,
      familyTreeResponseDTO.name,
      familyTreeResponseDTO.isPrivate,
      new Date(familyTreeResponseDTO.createdAt));
  }
}
