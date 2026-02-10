# Lab2ARSW Wait/Notify — Parte I (Calentamiento)

Descripción del ejercicio
- Tomar el programa PrimeFinder y modificarlo para que, cada t milisegundos:
  - Se pausen todos los hilos trabajadores.
  - Se muestre cuántos números primos se han encontrado.
  - El programa espere ENTER para reanudar.
- La sincronización debe usar synchronized, wait(), notify()/notifyAll() sobre el mismo monitor (sin busy-waiting).
- En el reporte de laboratorio se deben incluir observaciones sobre diseño de sincronización (qué lock, qué condición, cómo se evitan lost wakeups).

Qué se hizo en este repositorio
- Se modificó PrimeFinder para permitir pausar y reanudar los hilos trabajadores periódicamente desde un hilo de control.
- Control (clase `Control`) arranca N hilos `PrimeFinderThread`, cada TMILISECONDS pausa todos los hilos, muestra la cantidad de primos encontrados hasta el momento y espera ENTER para continuar.
- `PrimeFinderThread` implementa las operaciones `pauseThread()`, `resumeThread()` y `stopThread()` y utiliza wait()/notify() para suspensión/continuación.

Arquitectura del proyecto
- Paquete principal: `edu.eci.arsw.primefinder`
  - `Control`: hilo de control que orquesta pausas periódicas, muestra conteos y reanuda hilos.
  - `PrimeFinderThread`: hilo trabajador que busca primos en un rango y soporta pausar/reanudar mediante wait/notify.
- Código simple en Java sin frameworks adicionales.

Tecnologías
- Java (JDK 8+), API estándar (threads, IO).
- Proyecto orientado a consola.

Diseño de sincronización (observaciones)
- Lock: cada hilo trabajador usa su propio monitor (el objeto `this` de `PrimeFinderThread`). Las operaciones `pauseThread()`, `resumeThread()` y el bloque donde se llama `wait()` están sincronizados sobre el mismo monitor.
- Condición: un booleano `pause` en cada `PrimeFinderThread` indica si el hilo debe suspenderse. El bucle del `run()` hace:
  - synchronized(this) { while (pause && run) wait(); }
  - Usar `while` asegura que, al despertarse, se reevalúe la condición y así se evitan *lost wakeups* y falsas señales.
- Orden de operaciones para evitar races/lost wakeups:
  - `Control` invoca `pauseThread()` en cada hilo (método sincronizado que pone `pause=true`). Si un hilo aún no ha llegado al punto de espera, cuando llegue leerá `pause==true` y entrará en wait().
  - Para reanudar, `resumeThread()` (sincronizado) pone `pause=false` y llama a `notify()` (o podría usarse `notifyAll()` si se prefiere). Como la condición se comprueba dentro del while, si `notify()` ocurre antes de que el hilo ejecute wait(), el hilo no bloqueará porque `pause` ya será `false`.
  - Se añade una pequeña espera en el hilo de control tras pedir la pausa para dar tiempo a que los trabajadores alcancen el punto de suspensión antes de leer los conteos (esto simplifica evitar lecturas concurrentes del estado interno).
- Lecturas seguras: antes de mostrar los conteos, se intenta garantizar que los trabajadores estén en estado pausado. Para mayor robustez podría sincronizarse el acceso a la lista de primos o usar estructuras thread-safe; en esta implementación la pausa + breve espera se usa para minimizar contención.

Notas y recomendaciones
- Podría usarse `notifyAll()` en `resumeThread()` si los hilos compartieran el mismo monitor; aquí cada hilo tiene su propio monitor, por lo que `notify()` en el monitor del hilo es suficiente.
- Para un diseño más robusto, considerar:
  - Usar colecciones thread-safe (p. ej. `CopyOnWriteArrayList` o sincronizar acceso a `primes`) si se desea evitar depender de la pausa para leer el tamaño.
  - Evitar sleeps para sincronizar transiciones; implementar una condición adicional (p. ej. un contador de hilos pausados) si se requiere certeza absoluta de que todos los hilos están suspendidos antes de leer resultados.

Cómo ejecutar (rápido)
- Compilar y ejecutar desde consola con `javac`/`java` o desde el IDE. El hilo de control pedirá ENTER para reanudar después de cada pausa periódica.

Autor
- Julián Arenas
