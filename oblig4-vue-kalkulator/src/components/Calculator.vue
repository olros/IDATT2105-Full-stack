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

    const sumUp = () => {
      const sign = chosenSign.value;
      entireInput.value += `${currentInput.value} ${sign} `;
      if (sign === "+") {
        totalInput.value += Number(currentInput.value);
      } else if (sign === "-") {
        totalInput.value -= Number(currentInput.value);
      } else if (sign === "/") {
        totalInput.value /= Number(currentInput.value);
      } else if (sign === "*") {
        totalInput.value *= Number(currentInput.value);
      }
    };

    const setSign = (newSign: "+" | "-" | "/" | "*") => {
      if (hasInput.value && !chosenSign.value) {
        chosenSign.value = newSign;
        // entireInput.value += `${currentInput.value}`;
        currentInput.value = "";
      } else if (hasInput.value && chosenSign.value) {
        sumUp();
        chosenSign.value = null;
      }
    };

    const clickNumber = (newNumber: number | ".") => {
      if (!hasInput.value) {
        if (newNumber > 0 && currentInput.value.slice(-1) !== '.') {
          currentInput.value = `${newNumber}`;
        }
      } else if ((hasInput.value || newNumber > 0) && currentInput.value.slice(-1) !== '.') {
        currentInput.value += `${newNumber}`;
      }
    };

    const compute = () => {
      sumUp();
      entireInput.value += ` = ${totalInput.value}`;
      totalInput.value += Number(currentInput.value);
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
