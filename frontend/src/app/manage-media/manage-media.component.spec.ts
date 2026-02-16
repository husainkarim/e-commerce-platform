import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { ManageMediaComponent } from './manage-media.component';
import { ApiService } from '../api.service';

describe('ManageMediaComponent', () => {
  let component: ManageMediaComponent;
  let fixture: ComponentFixture<ManageMediaComponent>;
  let apiService: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', [
      'getProductById',
      'getImagesByProductId',
      'addmedia',
      'deleteImage'
    ]);

    apiService.getProductById.and.returnValue(of({ product: { id: 'p1' } }));
    apiService.getImagesByProductId.and.returnValue(of({ images: [{ imagePath: 'img1.png' }] }));
    apiService.addmedia.and.returnValue(of({ success: true }));
    apiService.deleteImage.and.returnValue(of({ success: true }));

    await TestBed.configureTestingModule({
      imports: [ManageMediaComponent],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: ActivatedRoute, useValue: { paramMap: of(convertToParamMap({ id: 'p1' })) } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ManageMediaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load images', () => {
    expect(component).toBeTruthy();
    expect(component.images.length).toBe(1);
    expect(component.images[0]).toBe('img1.png');
  });

  it('should set selected file', () => {
    const file = new File(['data'], 'file.txt');
    component.onFileSelected({ target: { files: [file] } });
    expect(component.selectedFile).toBe(file);
  });

  it('should set uploadError when no file selected', () => {
    component.selectedFile = null;
    component.addImage();
    expect(component.uploadError).toBeTrue();
  });

  it('should upload image and refresh list', () => {
    const file = new File(['data'], 'file.txt');
    component.selectedFile = file;

    component.addImage();

    expect(apiService.addmedia).toHaveBeenCalled();
    expect(component.uploadError).toBeFalse();
    expect(component.selectedFile).toBeNull();
    expect(apiService.getImagesByProductId).toHaveBeenCalledWith('p1');
  });

  it('should delete image and refresh list', () => {
    component.media = [{ id: 'img1' }];
    component.deleteFile(0);
    expect(apiService.deleteImage).toHaveBeenCalledWith({ id: 'img1' });
    expect(apiService.getImagesByProductId).toHaveBeenCalledWith('p1');
  });
});
