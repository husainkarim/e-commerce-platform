import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Forbidden } from './forbidden';
import { ActivatedRoute } from '@angular/router';

describe('Forbidden', () => {
  let component: Forbidden;
  let fixture: ComponentFixture<Forbidden>;

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
      imports: [Forbidden],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Forbidden);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
