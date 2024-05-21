# SelectSaveImagePicker

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

<img src="https://github.com/KeunyoungSong/SelectSaveImagePicker/assets/84883277/246bf62e-2761-4469-9d08-89faffc4c866" height="470"/>

SelectSaveImagePicker is an Android library for selecting and saving images with state preservation. This library retains the state of each image, providing a seamless user experience.

## Features

- Select multiple images with state preservation
- Customizable appearance and behavior
- Permission handling with rationale and settings redirection
- Support horizontal screen

## Installation

Add the following repository to your `settings.gradle.kts` file:

```kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // Add this
    }
}
```

Then add the dependency to your `build.gradle.kts` file:

```kts
dependencies {
	implementation ("com.github.KeunyoungSong:SelectSaveImagePicker:version")
}
```

## Usage

### Basic Usage

1. **Initialize the Picker**: Create an instance of `SelectSaveImagePicker` with the desired configuration.

```kotlin
val customPickerConfig = PickerConfig.Builder(this)
  .setMaxSelection(5)
  .setItemSpacing(12)
  .setIndicatorNumberColorHex("#FFFFFF")
  .setItemStrokeWidth(4)
  .setThemeColor(ContextCompat.getColor(this, color.themeColor))
  .setDescriptionText("Please select images")
  .setThumbnailScale(0.8f)
  .build()

val imagePicker = SelectSaveImagePicker.newInstance(customPickerConfig)
imagePicker.show(supportFragmentManager, "PICKER")
```

2. **Handle Selection**: Implement the `OnSelectionCompleteListener` to receive the selected images.

```kotlin
class MainActivity : AppCompatActivity(), SelectSaveImagePicker.OnSelectionCompleteListener {

    override fun onSelectionComplete(selectedImages: List<String>) {
        // Handle the selected images
        selectedImages.forEach { image ->
            Log.d("MainActivity", "Selected image: $image")
        }
    }
}
```

### Customization

You can customize the appearance and behavior of the image picker by setting various configuration options in the `PickerConfig` class.

## Code Overview

### SelectSaveImagePicker Fragment

The core of the library is the `SelectSaveImagePicker` fragment, which handles the image selection UI and state management.

```kotlin
class SelectSaveImagePicker : BottomSheetDialogFragment() {

    private var _binding: FragmentSelectSaveImagePickerBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SelectSaveImagePickerViewModel by activityViewModels {
        ViewModelFactory(
            maxSelection = config.maxSelection,
            repository = ImageRepository(requireContext())
        )
    }

    // Other implementation details...

}
```

### PermissionRequester Utility

The `PermissionRequester` class handles runtime permission requests and rationale display.

```kotlin
class PermissionRequester(
    private val fragment: Fragment,
    private val permission: String
) {

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var onPermissionChecked: (() -> Unit)? = null

    // Other implementation details...

}
```

### ImagePickerItemView

The `ImagePickerItemView` class represents each item in the image picker grid, handling selection state and display.

```kotlin
class ImagePickerItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ImagePickerItemViewBinding = ImagePickerItemViewBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    // Other implementation details...

}
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request on GitHub.

## License

This project is licensed under the MIT License.

## Contact

For any inquiries, please reach out to [rmsdud623@gmail.com].
