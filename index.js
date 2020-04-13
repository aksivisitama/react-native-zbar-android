import { NativeModules } from 'react-native';

const ZBar = NativeModules.ZBar;

// export default class ZBarComponent {
//
//   static startScan(options, cb) {
//     return ZBar.startScan(options, cb);
//   }
// }

module.exports = {
  startScan: (headerCamera, callback) => {
    return ZBar.startScan(headerCamera, callback);
  }
}
