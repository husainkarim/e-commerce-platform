import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NotAuthorizedComponent } from './not-authorized.component';
import { ActivatedRoute } from '@angular/router';

describe('NotAuthorizedComponent', () => {
  let component: NotAuthorizedComponent;
  let fixture: ComponentFixture<NotAuthorizedComponent>;

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
      imports: [NotAuthorizedComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotAuthorizedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
