import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UploadImageService } from '../upload-image.service';

@Component({
  selector: 'app-manage-media',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-media.component.html',
  styleUrl: './manage-media.component.css'
})
export class ManageMediaComponent {
  images: string[] = [
    'https://via.placeholder.com/150',
    'https://via.placeholder.com/160'
  ];
  newImageUrl: string = '';
  previewImage: string | null = null;
  uploadError: string | null = null;
  constructor(private uploadImageService: UploadImageService) {}
  addImage() {
    if (this.previewImage) {
      this.images.push(this.previewImage);
      this.previewImage = null;
      this.uploadError = null;
    } else if (this.newImageUrl.trim()) {
      this.images.push(this.newImageUrl.trim());
      this.newImageUrl = '';
    }
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      if (file.size > 2 * 1024 * 1024) {
        this.uploadError = 'Image size must be less than 2MB.';
        this.previewImage = null;
        return;
      }
      this.uploadImageService.uploadFile(file);

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.previewImage = e.target.result;
        this.uploadError = null;
      };
      reader.readAsDataURL(file);
    }
  }

  deleteImage(index: number) {
    this.images.splice(index, 1);
  }
}
