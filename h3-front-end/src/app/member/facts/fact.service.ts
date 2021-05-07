import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Fact, FactDTO, FactDTOsWrapper} from '../../shared/dtos.model';
import {environment} from '../../../environments/environment';
import {map} from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class FactService {
  constructor(private http: HttpClient) {
  }

  getAllFacts(treeId: number): Observable<Fact[]> {
    return this.http.get<FactDTOsWrapper>(`${environment.domain}/trees/${treeId}/facts`)
      .pipe(map(factDtos => {
          return factDtos.facts.map(factDto => this.factDTOToFact(factDto));
        }
      ));
  }

  private factDTOToFact(factDto: FactDTO): Fact {
    const date = factDto.date ? new Date(factDto.date) : null;
    return new Fact(
      factDto.id,
      factDto.name,
      factDto.description,
      date,
      +factDto.familyMemberId);
  }
}
