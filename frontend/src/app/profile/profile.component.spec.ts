import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfileComponent } from './profile.component';
import { ActivatedRoute } from '@angular/router'; // <--- NEW IMPORT

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;

  // --- NEW: Define a mock for ActivatedRoute ---
  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: (key: string) => 'user-id'
      }
    },
    params: {
        subscribe: () => ({})
    },
    queryParamMap: {
        subscribe: () => ({})
    }
  };
  // ---------------------------------------------

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [ // <--- NEW: Add the Providers array
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute // Provide the mock implementation
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
