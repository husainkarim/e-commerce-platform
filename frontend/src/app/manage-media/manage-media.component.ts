import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-manage-media',
  templateUrl: './manage-media.component.html',
  styleUrls: ['./manage-media.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class ManageMediaComponent {
  selectedFile: File | null = null;
  productId: string = '';
  images: string[] = []; // Array to hold image URLs
  media: any[] = [];
  uploadError: boolean = false;
  previewImage: boolean = false;
  constructor(private route: ActivatedRoute, private apiService: ApiService) { }

  ngOnInit(): void {
    // Subscribe so it reacts if the route param changes
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.productId = id;
        this.loadProductImages(id);
      }
    });
  }

  private loadProductImages(productId: string): void {
    this.apiService.getImagesByProductId(productId).subscribe({
      next: (response) => {
        this.media = response.images;
        this.images = response.images.map((img: { imagePath: string }) => img.imagePath);
      },
      error: (error) => {
        console.error('Failed to fetch product images:', error);
      }
    });
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  addImage() {
    if (!this.selectedFile) {
      this.uploadError = true;
      console.error("No file selected");
      return;
    }
    const formData = new FormData();
    formData.append('file', this.selectedFile);   // file
    formData.append('productId', this.productId);  // string or number
    this.apiService.addmedia(formData).subscribe(response => {
      console.log('Image uploaded successfully', response);
      this.uploadError = false;
      this.selectedFile = null; // Clear the selected file
      this.loadProductImages(this.productId);
    }, error => {
      console.error('Error uploading image', error);
      this.uploadError = true;
    });
  }

  deleteFile(index: number) {
    let mediaData = this.media[index];
    this.apiService.deleteImage(mediaData).subscribe(response => {
      console.log('Image deleted successfully', response);
      this.loadProductImages(this.productId);
    }, error => {
      console.error('Error deleting image', error);
    });
  }
}
