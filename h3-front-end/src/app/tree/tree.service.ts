import {FamilyTree, FamilyTreeDataDTO, FamilyTreeListDTO, FamilyTreeResponseDTO, MessageDTO} from '../shared/dtos.model';
import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {map, switchMap, take, tap} from 'rxjs/operators';
import {AuthService} from '../authentication/auth.service';
import {environment} from '../../environments/environment';

@Injectable({providedIn: 'root'})
export class TreeService {
  createdNewTree = new Subject<FamilyTree>();

  constructor(private http: HttpClient, private authService: AuthService) {
  }

  getOwnTrees(): Observable<FamilyTree[]> {
    return this.authService.user.pipe(
      take(1),
      switchMap(user => {
        return this.http.get<FamilyTreeListDTO>(environment.domain + '/trees/' + user.username)
          .pipe(map(familyTreeListDTO => {
            return familyTreeListDTO.familyTrees.map(familyTreeResponseDTO => {
              return this.mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO);
            });
          }));
      }));
  }

  createTree(familyTreeData: FamilyTreeDataDTO): Observable<FamilyTree> {
    return this.http.post<FamilyTreeResponseDTO>(environment.domain + '/trees/', familyTreeData)
      .pipe(map(familyTreeResponseDTO => {
        return this.mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO);
      }), tap(familyTree => {
        this.createdNewTree.next(familyTree);
      }));
  }

  findTree(treePattern: string): Observable<FamilyTree[]> {
    if (!treePattern) {
      treePattern = '';
    }

    return this.http.get<FamilyTreeListDTO>(environment.domain + '/trees/', {
      params: new HttpParams().append('treePattern', treePattern)
    })
      .pipe(map(familyTreeListDTO => {
        return familyTreeListDTO.familyTrees.map(familyTreeResponseDTO => {
          return this.mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO);
        });
      }));
  }

  getTree(treeId: number): Observable<FamilyTree> {
    return this.http.get<FamilyTreeResponseDTO>(environment.domain + '/trees/id/' + treeId)
      .pipe(map(familyTreeResponseDTO =>
        this.mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO)));
  }

  updateTree(tree: FamilyTree): Observable<FamilyTree> {
    return this.http.put<FamilyTreeResponseDTO>(environment.domain + '/trees/' + tree.id,
      new FamilyTreeDataDTO(tree.name,
        tree.isPrivate,
        tree.tags))
      .pipe(map(familyTreeResponseDTO =>
        this.mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO)));
  }

  deleteTree(id: number): Observable<MessageDTO> {
    return this.http.delete<MessageDTO>(`${environment.domain}/trees/${id}`);
  }

  private mapFamilyTreeResponseDTOToFamilyTree(familyTreeResponseDTO: FamilyTreeResponseDTO): FamilyTree {
    return new FamilyTree(familyTreeResponseDTO.id,
      familyTreeResponseDTO.name,
      familyTreeResponseDTO.isPrivate,
      new Date(familyTreeResponseDTO.createdAt),
      familyTreeResponseDTO.owner,
      familyTreeResponseDTO.tags.sort());
  }
}
