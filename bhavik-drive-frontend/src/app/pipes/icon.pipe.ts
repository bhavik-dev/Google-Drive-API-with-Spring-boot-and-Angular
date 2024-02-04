import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'icon'
})
export class IconPipe implements PipeTransform {

  transform( value : string ): string
  {
    if (value == 'application/vnd.google-apps.folder') {
      return 'folder';
    } else if (value.includes('image/')) {
      return 'image';
    } else if (value.includes('video/')) {
      return 'movie';
    } else if (value.includes('audio/')) {
      return 'audiotrack';
    } else if (value.includes('text/')) {
      return 'text_snippet';
    }else {
      return 'insert_drive_file';
    }
  }
}
