import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NotFoundComponent } from './not-found.component';
import { ActivatedRoute } from '@angular/router'; // <--- NEW IMPORT

describe('NotFoundComponent', () => {
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;

  // --- NEW: Define a minimal mock for ActivatedRoute ---
  const mockActivatedRoute = {
    // Error components often don't need parameters, but they need the service provided
    snapshot: {},
    params: {
        subscribe: () => ({})
    },
    queryParamMap: {
        subscribe: () => ({})
    }
  };
  // -----------------------------------------------------

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotFoundComponent],
      providers: [ // <--- NEW: Add the Providers array
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute // Provide the mock implementation
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
