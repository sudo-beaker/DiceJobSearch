import {Component, inject, signal} from '@angular/core';
import {JobListing, JobSearchService} from "../../service/job-search.service";
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-home-component',
  imports: [
    DatePipe
  ],
  templateUrl: './home-component.html',
  styleUrl: './home-component.css',
})
export class HomeComponent {
  private jobSearchService = inject(JobSearchService);

  protected jobs = signal<JobListing[]>([]);
  protected isLoading = signal(false);

  onHomeClick() {
    this.isLoading.set(true);
    this.jobSearchService.getJobs().subscribe({
      next: (response) => {
        this.jobs.set(response.results);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error fetching jobs', err);
        this.isLoading.set(false);
      }
    });
  }
}
