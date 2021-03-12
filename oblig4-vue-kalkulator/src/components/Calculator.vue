<template>
  <div class="root">
    <!-- <img src="" alt="Kalkulator" /> -->
    <h1>Kalkulator</h1>
    <p>Dette er en enkel kalkulator.</p>
    <div>
      <h3>{{ input }}</h3>
    </div>
    <div class="grid">
      <div class="numbersGrid">
        <CalcButton
          v-for="num in numbers"
          @click="() => clickNumber(num)"
          :key="num"
          :label="String(num)"
        ></CalcButton>
        <CalcButton @click="() => clickNumber('.')" label="."></CalcButton>
        <CalcButton @click="compute" label="="></CalcButton>
      </div>
      <CalcButton
        v-for="sign in signs"
        @click="() => setSign(sign)"
        :key="sign"
        :label="sign"
      ></CalcButton>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, ref, defineComponent } from "vue";
import CalcButton from "./CalcButton.vue";

export default defineComponent({
  name: "Calculator",
  components: {
    CalcButton,
  },
  setup() {
    const numbers = [...Array(10).keys(), 0].slice(1);
    const signs = ["+", "-", "/", "*"];
    const log = ref([]);
    const currentInput = ref("");
    const hasInput = computed(
      () => currentInput.value !== ""
        && currentInput.value !== null
        && currentInput.value !== "0",
    );
    const entireInput = ref("");
    const totalInput = ref(0);
    const chosenSign = ref<"+" | "-" | "/" | "*" | null>(null);

    const input = computed(
      () => `${currentInput.value || ""}`,
    );

    const sumUp = (withSign = true) => {
      entireInput.value += withSign ? `${currentInput.value} ${chosenSign.value} ` : currentInput.value;
      if (chosenSign.value === "+") {
        totalInput.value += Number(currentInput.value);
      } else if (chosenSign.value === "-") {
        totalInput.value -= Number(currentInput.value);
      } else if (chosenSign.value === "/") {
        totalInput.value /= Number(currentInput.value);
      } else if (chosenSign.value === "*") {
        totalInput.value *= Number(currentInput.value);
      }
    };

    const setSign = (newSign: "+" | "-" | "/" | "*") => {
      if (chosenSign.value && hasInput.value) {
        entireInput.value += `+ ${currentInput.value}`;
        chosenSign.value = null;
        currentInput.value = "";
      } else if (hasInput.value) {
        chosenSign.value = newSign;
      }
    };

    const clickNumber = (newNumber: number | ".") => {
      if (chosenSign.value && !hasInput.value) {
        sumUp();
        if (newNumber > 0) {
          currentInput.value = `${newNumber}`;
        }
      } else if (hasInput.value || newNumber > 0) {
        currentInput.value += `${newNumber}`;
      }
    };

    const compute = () => {
      sumUp(false);
      console.log(entireInput.value);
      console.log(totalInput.value);
    };

    return {
      log,
      input,
      numbers,
      signs,
      setSign,
      clickNumber,
      compute,
    };
  },
});
</script>

<style scoped>
h3 {
  margin: 40px 0 0;
}
.root {
  max-width: 500px;
  width: 100%;
  margin: 0 auto;
}
.grid {
  display: grid;
  grid-template-columns: 3fr 1fr;
  grid-gap: 10px;
}
.numbersGrid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-row: span 4;
  grid-gap: 10px;
}
</style>
