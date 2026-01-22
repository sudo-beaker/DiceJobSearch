import { Injectable, inject } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';

export interface JobListing {
  id: string;
  title: string;
  summary: string;
  companyName: string;
  jobLocation: string;
  detailsPageUrl: string;
  companyPageUrl: string;
  salary: string;
  employmentType: string;
  workplaceTypes: string;
  postedDate: string;
  easyApply: boolean;
  applied: boolean;
  rejected: boolean;
  diceId: string;
}

export interface SearchResponse {
  query: string;
  results: JobListing[];
}

@Injectable({
  providedIn: 'root'
})
export class JobSearchService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/jobsearch/api';

  getJobs(query?: string): Observable<SearchResponse> {
    let params = new HttpParams();
    if (query) {
      params = params.set('query', query);
    }

    return this.http.get<SearchResponse>(this.apiUrl, { params });
  }
}
