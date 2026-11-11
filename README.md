# Drawing Text Demo App

* [Russian](#russian)
* [English](#english)

## Russian

Демо-приложение рисования текста.

Позволяет попробовать разные простые политики рисования текста. Можете составлять свои, сочетая
комбинации из представленных, или вовсе придумывая с нуля. Но здесь все политики специально 
оставлены простыми.

### Описание приложения

В верхней части экрана расположено поле ввода текста, который будет отрисован. Кнопка ниже
позволяет быстро выбрать один из установленных заранее вариантов.

Ещё ниже расположен переключатель, позволяющий задать небольшое начальное увеличение текста.

Ниже расположен выбор политики рисования. Нажмите кнопку Info для просмотра описания выбранной,
или кнопку рисования для проверки результата.

Серый прямоугольник внизу — это условная максимально допустимая область рисования, в которой
будет размещаться текст. После размещения текст будет подсвечен прямоугольником другого цвета:
это реальная вычисленная область.

### Политики рисования

Основные политики рисования сгенерированы на основе описаний, расположенных в `docs`.

## English

A demo application for drawing text.

It lets you try different simple text-drawing policies. You can build your own, combining the
ones provided, or designing them entirely from scratch. But here all the policies are
deliberately kept simple.

### App description

At the top of the screen there is a text input field for the text to be drawn. The button
below it lets you quickly pick one of the preset variants.

Right below there is a switch that applies a slight initial text enlargement.

Then comes the drawing-policy selector. Press the Info button to view a description of the
selected policy, or the draw button to check the result.

The grey rectangle at the bottom is the nominal maximum drawing area in which the text will be
placed. Once placed, the text is highlighted with a rectangle of a different color: that is
the actual computed area.

### Drawing policies

The main drawing policies are generated based on the descriptions located in `docs`.