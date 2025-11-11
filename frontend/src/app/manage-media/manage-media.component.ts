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
  uploadError: boolean = false;
  previewImage: boolean = false;
  constructor(private route: ActivatedRoute, private apiService: ApiService) {
    if (this.route.snapshot.paramMap.has('id')) {
      this.productId = this.route.snapshot.paramMap.get('id')!;
      // Fetch existing images for the product
    }
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
      // Assuming the response contains the URL of the uploaded image
      if (response && response.imageUrl) {
        this.images.push(response.imageUrl);
      }
      this.uploadError = false;
      this.selectedFile = null; // Clear the selected file
    }, error => {
      console.error('Error uploading image', error);
      this.uploadError = true;
    });
  }

  deleteFile(index: number) {
    this.images.splice(index, 1);
  }
}
