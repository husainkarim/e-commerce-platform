import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ManageMediaComponent } from './manage-media.component';
import { ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs'; // <--- NEW: Import 'of' to create mock Observables
import { ApiService } from '../api.service'; // <--- Import the service being mocked (Adjust path if needed)

describe('ManageMediaComponent', () => {
  let component: ManageMediaComponent;
  let fixture: ComponentFixture<ManageMediaComponent>;

  // Define a minimal mock for the ActivatedRoute service
  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: (key: string) => '123'
      }
    },
    params: {
        subscribe: () => ({})
    }
  };

  // NEW MOCK: Define a mock for ApiService that returns an Observable
  const mockApiService = {
    // Assuming the component calls a method like getMedia() in ngOnInit
    // This mock ensures that call returns a valid Observable with .subscribe()
    getMedia: () => of([]), // Returns an Observable of an empty array
    // Add any other methods from ApiService that ManageMediaComponent uses
    deleteMedia: () => of(null)
  };


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageMediaComponent, HttpClientTestingModule],

      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        // 🐛 FIX: Provide the mock ApiService
        { provide: ApiService, useValue: mockApiService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageMediaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
