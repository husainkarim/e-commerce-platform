import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BadRequest } from './bad-request';
import { ActivatedRoute } from '@angular/router';

describe('BadRequest', () => {
  let component: BadRequest;
  let fixture: ComponentFixture<BadRequest>;

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
      imports: [BadRequest],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BadRequest);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
