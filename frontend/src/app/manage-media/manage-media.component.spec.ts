import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ManageMediaComponent } from './manage-media.component';
import { ActivatedRoute } from '@angular/router'; // <-- NEW: Import the service you need to mock
// NOTE: If ManageMediaComponent also uses ApiService/HttpClient, you'll need HttpClientTestingModule here too.

describe('ManageMediaComponent', () => {
  let component: ManageMediaComponent;
  let fixture: ComponentFixture<ManageMediaComponent>;

  // Define a minimal mock for the ActivatedRoute service
  const mockActivatedRoute = {
    // This mocks the common scenario where a component reads a parameter from the route snapshot
    snapshot: {
      paramMap: {
        get: (key: string) => '123' // Always returns a mock ID
      }
    },
    // This mocks the common scenario where a component subscribes to route params
    params: {
        subscribe: () => ({})
    }
  };


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      // 1. Keep the component import for Standalone components
      imports: [ManageMediaComponent],

      providers: [
        // 2. NEW: Provide the mock object for ActivatedRoute
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute
        }
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
