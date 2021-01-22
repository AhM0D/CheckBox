# CheckBox

[![Download](https://api.bintray.com/packages/teamteagroup/CheckBox/CircleCheckBox/images/download.svg) ](https://bintray.com/teamteagroup/CheckBox/CircleCheckBox/_latestVersion)

## CircleCheckBox：
<p align="center">
<img src="/screenShot/demo.gif" alt="screenshot" title="screenshot" width="500" height="400"/>
</p>

## Attrs

|attr|format|
|:--:|:--:|
|checkIconSize|dimension|
|borderSize|dimension|
|borderColor|color|
|checkIconColor|color|
|backgroundColor|color|
|strokeStartAngle|integer|
|strokeDegree|integer|

## Setup
- `CircleCheckBox` is available in the MavenCentral, so getting it as simple as adding it as a dependency
```gradle
implementation 'ir.teamtea:CircleCheckBox:{latest-version}'
```

## Usage

```java
checkBox = (CircleCheckBox) findViewById(R.id.circle_check_box);
checkBox.setListener(new CircleCheckBox.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(boolean isChecked) {
        // do something
    }
});
```

Customizing duration of animation in border and fading circle 
```java
checkBox.setDuration(duration);
```
Customizing duration of animation in creating and disappearing check icon
```java
checkBox.setCheckAnimDuration(duration);
```

Also you can use it like this
```java
new CircleCheckBox.Builder(context)
                .setBorderThickness(2)
                .setCheckThickness(4)
                .setBackGroundColor(backColor)
                .setCheckIconColor(checkColor)
                .setBorderColor(BorderColor)
                .setCircleStrokeStartAngle(180)
                .setCircleStrokeDegree(180)
                .setDuration(duration)
                .setCheckAnimDuration(duration)
                .setOnCheckedChangeListener(isChecked -> {

                })
                .build();
```

# Thanks
- [mome13](https://github.com/mome13) for helping to do some improvement.


## License

    Designed and developed by 2020 AhM0D

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
